package edu.colostate.cs464.dj.web;

import lombok.Data;

/**
 *
 * @author tim
 */
@Data
public class Song {

	private String title;
	private String artist;
	private String url;

	public Song(String title, String artist, String url) {
		this.title = title;
		this.artist = artist;
		this.url = url;
	}

	public static Song fromTitle(String title, String url) {
		String artist;
		if (title.contains("-")) {
			String[] split = title.split("\\-", 2);
			artist = split[0].trim();
			title = split[1].trim();
		} else {
			artist = "Unknown";
		}

		return new Song(title, artist, url);
	}

}
