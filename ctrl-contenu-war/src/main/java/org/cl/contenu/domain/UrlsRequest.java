package org.cl.contenu.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UrlsRequest {

	private List<Url> urls = new ArrayList<Url>();
	private Date dateNaissance;

}
