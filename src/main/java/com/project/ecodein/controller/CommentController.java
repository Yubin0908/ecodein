package com.project.ecodein.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.project.ecodein.entity.Admin;
import com.project.ecodein.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CommentController {

	private final CommentService commentService;

	public CommentController (CommentService commentService) {

		this.commentService = commentService;

	};

	@PostMapping ("/comment/add/{boardNo}")
	public String addNewComment (String comment, HttpSession session, int boardNo) {

		Admin admin = (Admin) session.getAttribute("admin");
		commentService.addComment (comment, admin, boardNo);
		return "redirect:/board/{boardNo}";

	}

	@PostMapping ("/board/{boardNo}/edit")
	public String commentEdit (Long id, String comment) {

		commentService.updateComment (id, comment);
		return "redirect:/board/{boardNo}";

	}

	@GetMapping ("/board/{boardNo}/delete/{id}")
	public String deleteComment (@PathVariable Long id) {

		commentService.deleteComment (id);
		return "redirect:/board/{boardNo}";

	}

}
