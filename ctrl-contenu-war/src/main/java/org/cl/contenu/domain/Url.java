package org.cl.contenu.domain;

import lombok.Data;

@Data
public class Url {

	public Url() {
		super();
	}

	public Url(String url) {
		super();
		this.url = url;
	}

	private String url;

}
