
package com.microservice.offerservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.microservice.offerservice.models.Offer;
import com.microservice.offerservice.repositories.OfferMongoRepository;
import com.microservice.offerservice.repositories.OfferSearchRepository;

@Controller
public class OfferController {

	public OfferController() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Autowired
	OfferMongoRepository OfferRepository;
	
	@Autowired
	OfferSearchRepository OfferSearchRepository;
	
	
	
	@RequestMapping("/home")
	public String home(Model model) {
		model.addAttribute("OfferList", OfferRepository.findAll());
		return "home";
	}
	
	
	@RequestMapping(value = "/addOffer", method = RequestMethod.POST)
	public String addOffer(@ModelAttribute Offer offer) {
		OfferRepository.save(Offer);
		return "redirect:home";
	}
	
	@RequestMapping(value="/search")
	public String search(Model model, @RequestParam String search) {
		model.addAttribute("OfferList", OfferSearchRepository.searchOffers(search));
		model.addAttribute("search", search);
		return "home";
	}

}
