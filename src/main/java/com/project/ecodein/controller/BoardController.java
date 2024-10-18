package com.project.ecodein.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.project.ecodein.common.Pagenation;
import com.project.ecodein.common.PagingButtonInfo;
import com.project.ecodein.dto.BoardDTO;
import com.project.ecodein.dto.CommentDTO;
import com.project.ecodein.entity.Comment;
import com.project.ecodein.service.BoardService;
import com.project.ecodein.service.CommentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping ("/board")
public class BoardController {

	private Logger log = LoggerFactory.getLogger (this.getClass ());

	private final BoardService boardService;
	private final CommentService commentService;
	public BoardController (BoardService boardService, CommentService commentService) {

		this.boardService = boardService;
		this.commentService = commentService;

	}

	// 상세페이지
	@GetMapping ("/{boardNo}")
	public String findBoardByNo (@PathVariable int boardNo, Model model) {

		BoardDTO board = boardService.findBoardByNo (boardNo);
		List<CommentDTO> commentList=commentService.findByBoardNo(boardNo);
		model.addAttribute ("board", board);
		model.addAttribute ("commentList", commentList);
		
		return "board/boardDetail";

	}

	// 목록
	@GetMapping ("/board")
	public String findBoardList (@PageableDefault Pageable pageable, Model model, String search) {

		Page<BoardDTO> boardList = boardService.findBoardList (pageable, search);

		PagingButtonInfo paging = Pagenation.getPagingButtonInfo (boardList);

		model.addAttribute ("paging", paging);
		model.addAttribute ("boardList", boardList);
		model.addAttribute ("search", search);

		return "board/board";

	}

	// 등록
	@GetMapping ("/add")
	public void addPage () {

	}

	@PostMapping ("/add")
	public String addNewBoard (BoardDTO newBoard) {

		boardService.addNewBoard (newBoard);

		return "redirect:/board/board";

	}

	// 수정
	@GetMapping ("/update/{boardNo}")
	public String update (@PathVariable int boardNo, Model model) {

		model.addAttribute ("board", boardService.findBoardByNo (boardNo));
		return "board/update";

	}

	@PostMapping ("/update/{boardNo}")
	public String updateBoard (BoardDTO updateBoard) {

		boardService.updateBoard (updateBoard);

		return "redirect:/board/" + updateBoard.getBoardNo ();

	}

	// 삭제
	@GetMapping ("/delete/{boardNo}")
	public String deleteBoard (@PathVariable int boardNo) {

		boardService.deleteBoard (boardNo);

		return "redirect:/board/board";

	}

}
