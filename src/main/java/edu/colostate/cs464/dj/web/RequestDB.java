package edu.colostate.cs464.dj.web;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * A crappy in-memory database for requests
 * @author tim
 */
@Accessors(fluent = true)
public class RequestDB {

	private static RequestDB instance;

	@Getter
	private final List<SongRequest> requests;

	private RequestDB() {
		requests = new ArrayList<>();
	}

	public static RequestDB get() {
		if (instance == null) {
			instance = new RequestDB();
		}

		return instance;
	}

	public void add(SongRequest request) {
		requests.add(request);
	}

	public void remove(SongRequest request) {
		requests.remove(request);
	}

	public SongRequest getById(String id) {
		for (SongRequest req : requests) {
			if (req.getId().equals(id)) {
				return req;
			}
		}

		return null;
	}

	public void clear() {
		requests.clear();
	}

}
