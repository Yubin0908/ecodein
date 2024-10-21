package com.project.ecodein.service;

import java.time.LocalDateTime;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.project.ecodein.dto.BoardDTO;
import com.project.ecodein.entity.Admin;
import com.project.ecodein.entity.Board;
import com.project.ecodein.entity.User;
import com.project.ecodein.repository.BoardRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class BoardService {

	private final BoardRepository boardRepository;
	private final ModelMapper modelMapper;

	public BoardService (BoardRepository boardRepository, ModelMapper modelMapper) {

		this.boardRepository = boardRepository;
		this.modelMapper = modelMapper;

	}

	public List<BoardDTO> mainPageBoardList (HttpSession session) {

		User user = (User) session.getAttribute ("user");

		if (user != null) {

			List<Board> boardList = boardRepository.findByUser (user);

			return boardList.stream ().map (board -> modelMapper.map (board, BoardDTO.class)).toList ();

		} else {

			List<Board> boardList = boardRepository.findAll ();
			return boardList.stream ().map (board -> modelMapper.map (board, BoardDTO.class)).toList ();

		}

	}

	// 상세페이지
	public BoardDTO findBoardByNo (int board_no) {

		Board board = boardRepository.findById (board_no).orElseThrow (IllegalArgumentException::new);
		// modelMapper를 이용하여 엔티티를 DTO로 변환하여 반환
		return modelMapper.map (board, BoardDTO.class);

	}
	// 목록

	public Page<BoardDTO> findBoardList (Pageable pageable, String search) {

		if (search == null) {

			// page 파라미터가 Pageable의 number 값으로 넘어오는데 해당 값이 조회시에는 인덱스 기준이 되어야 해서 -1 처리가 필요하다.
			pageable = PageRequest.of (pageable.getPageNumber (),
				pageable.getPageSize (),
				Sort.by ("boardNo").descending ());
			Page<Board> boardList = boardRepository.findAll (pageable);
			return boardList.map (board -> modelMapper.map (board, BoardDTO.class));

		} else {

			// page 파라미터가 Pageable의 number 값으로 넘어오는데 해당 값이 조회시에는 인덱스 기준이 되어야 해서 -1 처리가 필요하다.
			pageable = PageRequest.of (pageable.getPageNumber () ,
				pageable.getPageSize (),
				Sort.by ("board_no").descending ());
			Page<Board> boardList = boardRepository.findBoardByKeyword (search, pageable);
			return boardList.map (board -> modelMapper.map (board, BoardDTO.class));

		}

	}

	// 등록
	@Transactional
	public void addNewBoard (BoardDTO newBoard) {

		newBoard.setBoardDate (LocalDateTime.now ());
		boardRepository.save (modelMapper.map (newBoard, Board.class));

	}

	// 수정
	@Transactional
	public void updateBoard (BoardDTO updateBoard) {

		Board foundBoard = boardRepository.findById (updateBoard.getBoardNo ())
			.orElseThrow (IllegalArgumentException::new);
		foundBoard.setBoardTitle (updateBoard.getBoardTitle ());
		foundBoard.setBoardContent (updateBoard.getBoardContent ());
		foundBoard.setBoardDate (LocalDateTime.now ());

	}

	// 삭제
	@Transactional
	public void deleteBoard (Integer boardNo) {

		boardRepository.deleteById (boardNo);

	}

}
