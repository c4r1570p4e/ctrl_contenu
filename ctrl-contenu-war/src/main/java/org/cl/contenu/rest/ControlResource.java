package org.cl.contenu.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.cl.contenu.domain.Url;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/url")
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

		Years years = Years.yearsBetween(dDateNaissance, DateTime.now());
		int age = years.getYears();

		int ageMinUrl = Math.abs(url.getUrl().hashCode() % 100);

		int ageMin = 12;

		if (ageMinUrl <= 12) {
			ageMin = 12;
		} else if (ageMinUrl <= 14) {
			ageMin = 14;
		} else if (ageMinUrl <= 16) {
			ageMin = 16;
		} else {
			ageMin = 18;
		}

		if (age < ageMin) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

	}
}
