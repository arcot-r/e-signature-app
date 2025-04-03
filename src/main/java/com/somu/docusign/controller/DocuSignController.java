package com.somu.docusign.controller;

import org.springframework.web.bind.annotation.*;

import com.somu.docusign.service.DocuSignService;

import java.util.Map;

@RestController
@RequestMapping("/docusign")
public class DocuSignController {

	private final DocuSignService docuSignService;

	public DocuSignController(DocuSignService docuSignService) {
		this.docuSignService = docuSignService;
	}

	@GetMapping("/hello")
	public Map<String, String> hello() {
		return Map.of("message", "Welcome to Docusign integration");
	}

	@PostMapping("/send")
	public Map<String, String> sendDocument(@RequestParam String email, @RequestParam String name,
			@RequestParam String filePath) {
		try {
			String envelopeId = docuSignService.sendDocument(email, name, filePath);
			return Map.of("envelopeId", envelopeId);
		} catch (Exception e) {
			return Map.of("error", e.getMessage());
		}
	}
}
