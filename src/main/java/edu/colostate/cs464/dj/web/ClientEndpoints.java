package edu.colostate.cs464.dj.web;

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
	public List<String> test(@QueryParam("q") String query) throws IOException {
		return YouTubeAPI.get().search(query).stream()
				.map(result -> result.getSnippet().getTitle())
				.collect(Collectors.toList());
	}

}
