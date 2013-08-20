package org.cl.contenu.rest;

import javax.servlet.http.HttpServletResponse;

import org.cl.contenu.domain.Url;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/url")
public class ControlResource {

	@RequestMapping(method = RequestMethod.POST)
	public void postControlerUrl(Url url, HttpServletResponse response) {

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

}
