package edu.colostate.cs464.dj.web;

import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Video;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ClientEndpoints {

	@GET
	@Path("search")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Song> search(@QueryParam("q") String query) throws IOException {
		return YouTubeAPI.get().search(query).stream()
				.map(result -> {
					SearchResultSnippet s = result.getSnippet();
					return Song.fromTitle(
							s.getTitle(),
							"https://youtu.be/" + result.getId().getVideoId());
				})
				.collect(Collectors.toList());
	}

	@GET
	@Path("list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SongRequest> list() throws IOException {
		return RequestDB.get().requests();
	}

	@GET
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public SongRequest add(@QueryParam("url") String url) throws IOException {
		String id = YouTubeAPI.get().videoId(url);
		if (id == null) {
			throw new IllegalArgumentException("Bad YouTube URL: " + url);
		}

		SongRequest req = RequestDB.get().getById(id);
		if (req != null) {
			req.inc();
			return req;
		}

		Video v = YouTubeAPI.get().lookup(id);
		String title = v.getSnippet().getTitle();
		String artist;

		if (title.contains("-")) {
			String[] split = title.split("\\-", 2);
			artist = split[0].trim();
			title = split[1].trim();
		} else {
			artist = "Unknown";
		}

		req = new SongRequest(artist, title, id);
		RequestDB.get().add(req);
		return req;
	}

	@GET
	@Path("remove")
	@Produces(MediaType.APPLICATION_JSON)
	public void remove(@QueryParam("id") String id) {
		SongRequest req = RequestDB.get().getById(id);
		if (req != null) {
			RequestDB.get().remove(req);
		} else {
			throw new IllegalArgumentException("Unknown ID: " + id);
		}
	}

	@GET
	@Path("clear")
	@Produces(MediaType.APPLICATION_JSON)
	public void clear() {
		RequestDB.get().clear();
	}

}
