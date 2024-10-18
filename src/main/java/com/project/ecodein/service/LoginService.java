package com.project.ecodein.service;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import com.project.ecodein.config.SecurityConfig;
import com.project.ecodein.dto.AdminDTO;
import com.project.ecodein.dto.BuyerDTO;
import com.project.ecodein.dto.UserDTO;
import com.project.ecodein.entity.Admin;
import com.project.ecodein.entity.User;
import com.project.ecodein.repository.AdminRepository;
import com.project.ecodein.repository.BuyerRepository;
import com.project.ecodein.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginService {

	private final UserRepository USER_REPOSITORY;
	private final AdminRepository ADMIN_REPOSITORY;
	private final SecurityConfig SECURITY_CONFIG;
	private final HttpSession HTTP_SESSION;
	private final BuyerRepository BUYER_REPOSITORY;
	private final ModelMapper MODEL_MAPPER;
	
	@Autowired
	public LoginService (UserRepository USER_REPOSITORY,
		AdminRepository ADMIN_REPOSITORY,
		SecurityConfig SECURITY_CONFIG,
		BuyerRepository BUYER_REPOSITORY,
		HttpSession HTTP_SESSION,
		ModelMapper MODEL_MAPPER) {

		this.USER_REPOSITORY = USER_REPOSITORY;
		this.ADMIN_REPOSITORY = ADMIN_REPOSITORY;
		this.SECURITY_CONFIG = SECURITY_CONFIG;
		this.BUYER_REPOSITORY = BUYER_REPOSITORY;
		this.HTTP_SESSION = HTTP_SESSION;
		this.MODEL_MAPPER = MODEL_MAPPER;
	}
	
	//보안코드 검사후 회원가입.
	public String signUp (UserDTO userDTO, String buyer_secure_code, Model model) {
		if (USER_REPOSITORY.findByUserId (userDTO.getUserId ()).isPresent ()) {
			model.addAttribute ("message", "이미 가입된 아이디입니다.");
			model.addAttribute ("url", "/signup");
			return "errorMessage";
		}
		if (USER_REPOSITORY.findByUserEmail (userDTO.getUserEmail ()).isPresent ()) {
			model.addAttribute ("message", "이미 가입된 이메일입니다.");
			model.addAttribute ("url", "/signup");
			return "errorMessage";
		}
		
		
		if (userDTO.getBuyerCode ().getBuyerSecureCode ().equals (buyer_secure_code)) {
			userDTO.setUserPassword (SECURITY_CONFIG.passwordEncoder ().encode (userDTO.getUserPassword ()));
			USER_REPOSITORY.save (MODEL_MAPPER.map (userDTO, User.class));
			return "redirect:/";
		} else {
			model.addAttribute ("message", "잘못된 보안코드입니다. 다시 확인해주세요.");
			model.addAttribute ("url", "/signup");
			return "errorMessage";
		}
	}
	
	public String adminSignUp (AdminDTO adminDTO, Model model) {
		if (ADMIN_REPOSITORY.findByAdminId (adminDTO.getAdminId ()).isPresent ()) {
			model.addAttribute ("message", "이미 가입된 아이디입니다.");
			model.addAttribute ("url", "/signup/admin");
			return "errorMessage";
		}
		adminDTO.setAdminPassword (SECURITY_CONFIG.passwordEncoder ().encode (adminDTO.getAdminPassword ()));
		Admin admin = MODEL_MAPPER.map (adminDTO, Admin.class);
		ADMIN_REPOSITORY.save (admin);
		return "redirect:/";
	}
	
	public String login (String user_id, String user_password, Model model) {
		
		if (USER_REPOSITORY.findByUserId (user_id).isPresent ()) {
			
			if (SECURITY_CONFIG.passwordEncoder ().matches (user_password, 
				USER_REPOSITORY.findByUserId (user_id).get ().getUserPassword ())) {
				
				HTTP_SESSION.setAttribute ("user", USER_REPOSITORY.findByUserId (user_id).get ());
				return "redirect:/main";
			} else {
				model.addAttribute ("message", "아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.");
				model.addAttribute ("url", "/");
				return "errorMessage";
			}
		} else {
			model.addAttribute ("message", "아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.");
			model.addAttribute ("url", "/");
			return "errorMessage";
		}
	}
	
	public String adminLogin (String user_id, String user_password, Model model) {
		if (ADMIN_REPOSITORY.findByAdminId (user_id).isPresent ()) {
			
			if (SECURITY_CONFIG.passwordEncoder ().matches (user_password, 
				ADMIN_REPOSITORY.findByAdminId (user_id).get ().getAdminPassword ())) {
				
				if(ADMIN_REPOSITORY.findByAdminId (user_id).get ().isAdminRecognize () == false) {
					model.addAttribute ("message", "관리자 권한이 없는 계정입니다.");
					model.addAttribute ("url", "/");
					return "errorMessage";
				} else {
					HTTP_SESSION.setAttribute ("admin", ADMIN_REPOSITORY.findByAdminId (user_id).get ());
					return "redirect:/main";
				}
			} else {
				model.addAttribute ("message", "아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.");
				model.addAttribute ("url", "/");
				return "errorMessage";
			}
		} else {
			model.addAttribute ("message", "아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.");
			model.addAttribute ("url", "/");
			return "errorMessage";
		}
	}
	
	public Page<BuyerDTO> searchBuyers (String name, int page, int pageSize) {
		Pageable pageable = PageRequest.of (page -1 , pageSize, Sort.by (Sort.Order.asc ("buyerName")));

		if (name == null || name.isEmpty ()) {
			// return BUYER_REPOSITORY.findAllAcitve (pageable);
            return BUYER_REPOSITORY.findByBuyerStatus(pageable, true).map (b -> MODEL_MAPPER.map (b, BuyerDTO.class));
		} else {
			return BUYER_REPOSITORY.findByBuyerNameActive (name, pageable).map (b -> MODEL_MAPPER.map (b, BuyerDTO.class));
		}
	}
}
