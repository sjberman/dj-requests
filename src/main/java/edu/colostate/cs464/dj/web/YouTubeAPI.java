package edu.colostate.cs464.dj.web;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import lombok.extern.apachecommons.CommonsLog;

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


}
