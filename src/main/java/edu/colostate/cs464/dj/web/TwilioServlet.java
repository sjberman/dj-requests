package edu.colostate.cs464.dj.web;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.twilio.sdk.verbs.Message;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class TwilioServlet extends HttpServlet {

	private String handleMessage(String text) throws IOException {
		text = text.trim();

		SongRequest request = null;
		String id = YouTubeAPI.get().videoId(text);
		if (id != null) {
			request = RequestDB.get().getById(id);
			if (request != null) {
				request.inc();
				return String.format("Okay, request added for %s - %s",
						request.getTitle(), request.getArtist());
			}

			Video video = YouTubeAPI.get().lookup(id);
			if (video != null) {
				Song song = Song.fromTitle(video.getSnippet().getTitle(), id);
				RequestDB.get().add(new SongRequest(song.getArtist(), song.getTitle(), id));
				return String.format("Okay, request added for %s - %s",
						song.getTitle(), song.getArtist());
			}
		}

		List<SearchResult> results = YouTubeAPI.get().search(text);
		if (!results.isEmpty()) {
			SearchResult first = results.get(0);
			Song song = Song.fromTitle(first.getSnippet().getTitle(), first.getId().getVideoId());
			RequestDB.get().add(new SongRequest(song.getArtist(), song.getTitle(), song.getUrl()));
			return String.format("Okay, request added for %s - %s",
					song.getTitle(), song.getArtist());
		}

		return "Sorry, no results found!";
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("Got request: " + request.getParameterMap());
		TwiMLResponse twiml = new TwiMLResponse();

		String body = request.getParameter("Body");
		String responseText;
		try {
			responseText = handleMessage(body);
		} catch (IOException ex) {
			ex.printStackTrace();
			responseText = "Sorry, an error occurred!";
		}

		Message message = new Message(responseText);
		try {
			twiml.append(message);
		} catch (TwiMLException e) {
			e.printStackTrace();
		}

		response.setContentType("application/xml");
		response.getWriter().print(twiml.toXML());
	}

}
