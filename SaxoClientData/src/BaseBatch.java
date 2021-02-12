

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import za.co.sharenet.snbasic.Oracle;

public class BaseBatch {

	public static final String endpoint = "https://gateway.saxobank.com/openapi/";
	public static int reqId;
	private String LINE_FEED = "\r\n";

	public StringBuffer addBatch(List<String> path, StringBuffer sb, String reqId, int batchSize, int batchNo) {

		for (String s : path) {

		sb.append(LINE_FEED);
		sb.append("Content-Type:application/http; msgtype=request");
		sb.append(LINE_FEED);
		sb.append(LINE_FEED);
		sb.append("GET /openapi/" + s + " HTTP/1.1");
		sb.append(LINE_FEED);
		sb.append("X-Request-Id:" + reqId);
		sb.append(LINE_FEED);
		sb.append("Accept-Language:en");
		sb.append(LINE_FEED);
		sb.append("Host:gateway.saxobank.com");
		sb.append(LINE_FEED);
		sb.append(LINE_FEED);
		sb.append(LINE_FEED);


			sb.append(("--+"));
			// else
			// sb.append("--+--");
	}
		return sb;
	}

	public Map<String, List<String>> sendBatchRequest(Map<String, List<String>> batchMap,
			String serviceGroup,
			String accountType) {
		Map<String, List<String>> responseMap = new HashMap<String, List<String>>();
		String response = getBatchData(accountType, serviceGroup, batchMap);
		responseMap = parseResponse(response);
		return responseMap;
	}

	private HttpPost addHeaders(HttpPost httpPost, String token) {

		httpPost.addHeader("Content-Type", "multipart/mixed; boundary=\"+\"");
		httpPost.addHeader("Accept", "*/*");
		httpPost.addHeader("Accept-Language", "en, *;q=0.5");
		httpPost.addHeader("Cache-Control", "no-cache");
		httpPost.addHeader("User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36");
		httpPost.addHeader("Authorization", "Bearer " + token);
		httpPost.addHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.addHeader("Sec-Fetch-Mode", "cors");
		httpPost.addHeader("Sec-Fetch-Site", "cross-site");
		httpPost.addHeader("Sec-Fetch-Dest", "empty");

		return httpPost;
	}



	public String getBatchData(String dataType, String serviceGroup, Map<String, List<String>> endPointMap) {

		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			String tokenVendor = "";

			// System.out.println("dataType: " + dataType);

			if (dataType.equals("LOCAL")) {
				tokenVendor = "saxoLIVE";
			} else if (dataType.equals("OFFSHORE")) {
				tokenVendor = "saxoOFFSHORE";
			} else if (dataType.equals("DEV")) {
				tokenVendor = "saxoDEV";
			}
			String token = getSaxoToken(tokenVendor);
			HttpPost httpPost = new HttpPost(endpoint + serviceGroup + "/batch");
			addHeaders(httpPost, token);

			StringBuffer body = new StringBuffer();
			body.append("--+");
			int cnt = 0;

			
			Iterator<Map.Entry<String, List<String>>> it = endPointMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, List<String>> entry = it.next();
				addBatch(entry.getValue(), body, entry.getKey(), endPointMap.size(), cnt++);
			}

			/*
			 * for (String endPoint : endPoints) { addBatch(endPoint, body); cnt += 1; if
			 * (cnt < endPoints.size()) body.append(("--+")); else body.append("--+--"); }
			 */
			// body.append("--+--");
			String sbody = StringUtils.chop(body.toString());

			sbody += "+--";
			// System.out.println("Body:" + sbody);
			HttpEntity stringEntity = new StringEntity(sbody);
			httpPost.setEntity(stringEntity);
			CloseableHttpResponse response2 = httpclient.execute(httpPost);
			HttpEntity entity = response2.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			// System.out.println("Response:" + responseString);
			return responseString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getSaxoToken(String vendor) {
		String token = "";

		String sql = "SELECT access_token FROM trade.saxo_token WHERE login = ?";

		try (Connection conn = Oracle.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);) {
			ps.setString(1, vendor);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {

				token = rs.getString("ACCESS_TOKEN");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return token;
	}



	private Map<String, List<String>> parseResponse(String response) {

		BufferedReader br = new BufferedReader(new StringReader(response));

		List<String> lines = br.lines().collect(Collectors.toList());
		Map<String, List<String>> responseMap = new HashMap<String, List<String>>();
		int reqLineCnt = 0;
		boolean reqIdFound = false;
		String reqId = null;
		for (String line : lines) {

			if (line.startsWith("X-Request-Id")) {
				reqId = line.substring(line.indexOf(":") + 1).trim();
				reqIdFound = true;
			}
			if (reqIdFound) {
				reqLineCnt++;
			}
			if (reqLineCnt == 5) {
				responseMap.computeIfAbsent(reqId, k -> new ArrayList<String>()).add(line);
				reqIdFound = false;
				reqLineCnt = 0;
			}
		}

		responseMap.forEach((k, v) -> System.out.println(k + ":" + v));
		return responseMap;
	}

	public Map<String, List<String>> buildBatchMap(Map<String, List<String>> map, String key, String value) {

		map.computeIfAbsent(key, k -> new ArrayList<String>()).add(value);

		return map;

	}
}
