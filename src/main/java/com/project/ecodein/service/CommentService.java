package com.project.ecodein.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.ecodein.dto.CommentDTO;
import com.project.ecodein.entity.Admin;
import com.project.ecodein.entity.Board;
import com.project.ecodein.entity.Comment;
import com.project.ecodein.repository.BoardRepository;
import com.project.ecodein.repository.CommentRepository;

@Service
public class CommentService {

	private final CommentRepository commentRepository;
	private final ModelMapper modelMapper;
	private final BoardRepository boardRepository;

	public CommentService (CommentRepository commentRepository, ModelMapper modelMapper, BoardRepository boardRepository) {

		this.commentRepository = commentRepository;
		this.modelMapper = modelMapper;
		this.boardRepository = boardRepository;

	}


	public List<CommentDTO> findByBoardNo (int boardNo) {
		
		Board board =boardRepository.findById (boardNo).orElseThrow (IllegalArgumentException::new);
		List<Comment> comment = commentRepository.findByBoardNo (board);
		
		return comment.stream ().map (commentContent -> modelMapper.map (commentContent, CommentDTO.class)).toList ();

	}

	@Transactional
	public void addComment (String comment, Admin admin, int boardNo) {
		
		Optional<Board> board = boardRepository.findById (boardNo);
		Comment commentContent = new Comment();
		commentContent.setAdmin (admin);
		commentContent.setComment (comment);
		commentContent.setBoardNo (board.get());
		commentContent.setCreatedDate (LocalDateTime.now ());
		
		commentRepository.save (modelMapper.map (commentContent, Comment.class));
		
	}
	
	public void updateComment (Long id, String comment) {
		
		Optional<Comment> commentContent = commentRepository.findById (id);
		commentContent.get ().setComment (comment);
		commentContent.get ().setModifiedDate (LocalDateTime.now ());
		System.out.println (commentContent);
		
		commentRepository.save (modelMapper.map (commentContent, Comment.class));
		
	}
	
	@Transactional
	public void deleteComment (Long id) {
		
		commentRepository.deleteById (id);

	}





}