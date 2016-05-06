package edu.colostate.cs464.dj.web;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author tim
 */
@Data
public class SongRequest {

	private String artist;
	private String title;
	private String id;
	private int count;

	public SongRequest(String artist, String title, String id, int count) {
		this.artist = artist;
		this.title = title;
		this.id = id;
		this.count = count;
	}

	public SongRequest(String artist, String title, String id) {
		this.artist = artist;
		this.title = title;
		this.id = id;

		count = 1;
	}

	public void inc() {
		count++;
	}

}
