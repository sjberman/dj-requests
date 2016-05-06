package edu.colostate.cs464.dj.web;

import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 *
 * @author tim
 */
@CommonsLog
public class YouTubeAPI {



	public static final String CONFIG_PATH = "api.properties";

	private static YouTubeAPI instance;

	private Properties properties;
	private YouTube youtube;

	private YouTubeAPI() {
		properties = new Properties();

		Path config = Paths.get(CONFIG_PATH);
		try (BufferedReader br = Files.newBufferedReader(config)) {
			properties.load(br);
		} catch (IOException ex) {
			log.error("Could not load YouTube API keys from api.properties", ex);
		}

		NetHttpTransport http = new NetHttpTransport();
		JacksonFactory jackson = new JacksonFactory();
		youtube = new YouTube.Builder(http, jackson, (r) -> {})
				.setApplicationName("cs464-dj")
				.build();
	}

	public static YouTubeAPI get() {
		if (instance == null) {
			instance = new YouTubeAPI();
		}

		return instance;
	}

	public List<SearchResult> search(String query) throws IOException {
		YouTube.Search.List search = youtube.search().list("id,snippet");
		search.setKey(properties.getProperty("key"));
		search.setType("video");
		search.setVideoCategoryId("10");
		search.setMaxResults(5L);
		search.setQ(query);

		return search.execute().getItems();
	}

	public Video lookup(String id) throws IOException {
		YouTube.Videos.List listing = youtube.videos().list("snippet");
		listing.setKey(properties.getProperty("key"));
		listing.setId(id);

		List<Video> result = listing.execute().getItems();
		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public String videoId(String url) {
		try {
			URI uri = new URI(url);

			String host = uri.getHost().toLowerCase();
			if (host.startsWith("www.")) {
				host = host.substring(4);
			}

			// make sure we're looking at a youtube url
			if (host.equals("youtube.com") || host.equals("www.youtube.com")) {
				List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
				for (NameValuePair p : params) {
					if (p.getName().equalsIgnoreCase("v")) {
						return p.getValue();
					}
				}

				// no v= found
				return null;
			} else if (host.equals("youtu.be")) {
				return uri.getPath().substring(1); // in theory
			} else {
				return null;
			}
		} catch (URISyntaxException ex) {
			return null;
		}
	}

	public Video lookupUrl(String url) throws IOException {
		String id = videoId(url);
		if (id == null) {
			return null;
		} else {
			return lookup(id);
		}
	}

}
