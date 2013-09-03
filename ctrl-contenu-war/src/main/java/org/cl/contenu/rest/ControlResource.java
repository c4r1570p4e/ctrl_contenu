package org.cl.contenu.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.cl.contenu.domain.Url;
import org.cl.contenu.domain.UrlsRequest;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/url", produces = "application/json")
public class ControlResource {

	@RequestMapping(method = RequestMethod.POST)
	public void postControlerUrl(Url url, String dateNaissance, HttpServletResponse response) throws IOException {

		if (url == null || url.getUrl() == null || url.getUrl().trim().isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, IConstantesCodeErreur.URL_ABSENTE);
			return;
		}

		DateTime dDateNaissance = null;

		try {
			dDateNaissance = DateTime.parse(dateNaissance, DateTimeFormat.forPattern("dd-MM-yyyy"));
		} catch (IllegalArgumentException iae) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, IConstantesCodeErreur.ERREUR_FORMAT_DATE_NAISSANCE);
			return;
		}

		if (isUrlBloquee(url, dDateNaissance)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

	}

	@RequestMapping(value = "/multi", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public List<Url> postControlerUrls(@RequestBody UrlsRequest urlsRequest, HttpServletResponse response)
			throws IOException {

		if (urlsRequest == null || urlsRequest.getUrls() == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, IConstantesCodeErreur.URLS_REQUEST_ABSENTE);
			return null;
		}

		if (urlsRequest.getDateNaissance() == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, IConstantesCodeErreur.ERREUR_FORMAT_DATE_NAISSANCE);
			return null;
		}

		DateTime dDateNaissance = new DateTime(urlsRequest.getDateNaissance().getTime());

		List<Url> urls = new ArrayList<>();

		for (Url u : urlsRequest.getUrls()) {
			if (!isUrlBloquee(u, dDateNaissance)) {
				urls.add(u);
			}
		}

		return urls;

	}

	private boolean isUrlBloquee(Url url, DateTime dateNaissance) {
		return (getAge(dateNaissance) < getAgeMinUrl(url));
	}

	private int getAgeMinUrl(Url url) {
		int ageMinUrl = Math.abs(url.getUrl().hashCode() % 100);

		int ageMin = 0;

		if (ageMinUrl <= 6) {
			ageMin = 0;
		} else if (ageMinUrl <= 12) {
			ageMin = 12;
		} else if (ageMinUrl <= 14) {
			ageMin = 14;
		} else if (ageMinUrl <= 16) {
			ageMin = 16;
		} else {
			ageMin = 18;
		}

		return ageMin;
	}

	private int getAge(DateTime dateNaissance) {
		Years years = Years.yearsBetween(dateNaissance, DateTime.now());
		return years.getYears();
	}
}
