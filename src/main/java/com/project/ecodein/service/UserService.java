package com.project.ecodein.service;

import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.project.ecodein.dto.AdminDTO;
import com.project.ecodein.dto.UserDTO;
import com.project.ecodein.entity.User;
import com.project.ecodein.repository.AdminRepository;
import com.project.ecodein.repository.UserRepository;

@Service
public class UserService {

    private final AdminRepository ADMIN_REPOSITORY;
    private final UserRepository USER_REPOSITORY;
    private final PasswordEncoder PASSWORD_ENCODER;
    private final ModelMapper MODEL_MAPPER;

    public UserService(AdminRepository adminRepository, UserRepository userRepository, 
    	PasswordEncoder passwordEncoder, ModelMapper MODEL_MAPPER) {
        this.ADMIN_REPOSITORY = adminRepository;
        this.USER_REPOSITORY = userRepository;
        this.PASSWORD_ENCODER = passwordEncoder;
        this.MODEL_MAPPER = MODEL_MAPPER;
    }

    // [240927] 장유빈 기능 추가
    // 기능 상세 : 관리자 메뉴 -> 관리자 승인 요청 조회 기능
    public List<AdminDTO> findAllByAdminRecognize () {
        return ADMIN_REPOSITORY.findByAdminRecognize().stream ()
        	.map (a -> MODEL_MAPPER.map (a, AdminDTO.class)).toList ();
    }

    public void adminAuthPass (String adminId) {
        ADMIN_REPOSITORY.updateAdminAuthByAdminId(adminId);
    }

    // [240930] 장유빈 기능 추가
    // 기능 상세 : 비밀번호 변경 -> 비밀번호 확인 기능
    public Optional<UserDTO> userPasswordCheck (String user_id, String user_password) {
        User user = USER_REPOSITORY.findById(user_id).get();
        UserDTO userDTO = MODEL_MAPPER.map (user, UserDTO.class);
        
        String _password = user.getUserPassword();

        if (PASSWORD_ENCODER.matches(user_password, _password)) {
            return Optional.of(userDTO);
        } else {
            return Optional.empty();
        }
    }

    // [240930] 장유빈 기능 추가
    // 기능 상세 : 비밀번호 변경 -> 비밀번호 변경 기능
    public Integer userPasswordModify (String user_id, String user_password) {
        String newPassword = PASSWORD_ENCODER.encode(user_password);

        return USER_REPOSITORY.updatePassword(user_id, newPassword);
    }

    // [241013] 장유빈 기능 추가
    // 기능 상세 : 반품 관리 페이지에 해당 거래처 담당자 리스트
    public List<UserDTO> findAllByBuyerCode(int buyer_code) {
        List<User> users = USER_REPOSITORY.findAllByBuyerCode(buyer_code);
        return users.stream().map(user -> MODEL_MAPPER.map (user, UserDTO.class)).toList ();
    }
}
