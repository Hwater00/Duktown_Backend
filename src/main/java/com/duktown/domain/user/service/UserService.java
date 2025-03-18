package com.duktown.domain.user.service;

import com.duktown.domain.chat.dto.ChatDto;
import com.duktown.domain.chat.entity.Chat;
import com.duktown.domain.chat.entity.ChatRepository;
import com.duktown.domain.chatRoomUser.entity.ChatRoomUser;
import com.duktown.domain.chatRoomUser.entity.ChatRoomUserRepository;
import com.duktown.domain.cleaningUnit.entity.CleaningUnitInitDB;
import com.duktown.domain.roommate.entity.Roommate;
import com.duktown.domain.roommate.entity.RoommateRepository;
import com.duktown.domain.emailCert.dto.EmailCertDto;
import com.duktown.domain.emailCert.entity.EmailCert;
import com.duktown.domain.emailCert.entity.EmailCertRepository;
import com.duktown.domain.unitUser.entity.UnitUser;
import com.duktown.domain.unitUser.entity.UnitUserRepository;
import com.duktown.domain.user.dto.UserDto;
import com.duktown.domain.user.entity.User;
import com.duktown.domain.user.entity.UserRepository;
import com.duktown.global.email.MailService;
import com.duktown.global.exception.CustomException;
import com.duktown.global.security.provider.JwtTokenProvider;
import com.duktown.global.type.ChatRoomUserType;
import com.duktown.global.type.ChatType;
import com.duktown.global.type.UnitUserType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import static com.duktown.global.exception.CustomErrorType.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final EmailCertRepository emailCertRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    // 데모버전용
    private final RoommateRepository roommateRepository;
    private final UnitUserRepository unitUserRepository;

    private final CleaningUnitInitDB cleaningUnitInitDB;

    // 아이디 중복 체크 메서드
    public UserDto.IdCheckResponse idCheck(UserDto.IdCheckRequest idCheckRequest) {
        User user = userRepository.findByLoginId(idCheckRequest.getLoginId())
                .orElse(null);

        if (user != null) {
            return new UserDto.IdCheckResponse(true);
        }
        return new UserDto.IdCheckResponse(false);
    }

    // 사용자 회원가입 메서드
    @Transactional
    public UserDto.SignUpResponse signup(UserDto.SignupRequest signupRequest) {

        /*
        * 회원가입 이전 과정이 이메일, 아이디 중복 체크이지만,
        * 전 과정 없이 이 요청만 단독으로 들어오는 경우를 예방하기 위해 signup 요청때도 다시 한번 중복 체크를 수행한다.
        */
        // 이메일 중복 체크
        emailDuplicateCheck(signupRequest.getEmail());

        // 아이디 중복 체크
        idDuplicateCheck(signupRequest.getLoginId());

        // 이메일 인증 여부 체크
        EmailCert emailCert = emailCertRepository.findByEmail(signupRequest.getEmail()).orElseThrow(
                () -> new CustomException(EMAIL_CERT_NOT_FOUND)
        );

        if (!emailCert.getCertified()) {
            throw new CustomException(EMAIL_CERT_FAILED);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // 사용자 등록
        User user = signupRequest.toEntity(encodedPassword);
        User save = userRepository.save(user);

        //TODO: 유닛 배정(데모버전)
        Roommate roommate = roommateRepository.findFirstByOrderByIdDesc().orElseThrow(() -> new CustomException(UNIT_NOT_FOUND));
        unitUserRepository.save(UnitUser.builder().user(user).roommate(roommate).unitUserType(UnitUserType.UNIT_LEADER).build());
        //TODO: 청소 배정(데모버전)
        cleaningUnitInitDB.allocationCleaning(save.getId());


        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getId(), user.getRoleType());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId(), user.getId(), user.getRoleType());
        user.updateRefreshToken(refreshToken);
        return new UserDto.SignUpResponse(accessToken, refreshToken);
    }

    // 비밀번호 찾기 - 이메일로 계정 존재여부 확인
    public void userEmailExists(EmailCertDto.EmailRequest request) {
        userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    @Transactional
    // 임시 비밀번호 전송
    public void temporaryPwdEmailSend(UserDto.EmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 임시 비밀번호 생성
        String temporaryPwd = createTemporaryPwd();

        // 사용자 비밀번호 업데이트
        user.updatePassword(passwordEncoder.encode(temporaryPwd));

        String subject = "[덕타운] 임시 비밀번호 발급 안내";
        String text = "임시 비밀번호를 발급했습니다.\n\n" +
                "임시 비밀번호: " + temporaryPwd + "\n\n" +
                "해당 비밀번호로 로그인 후 반드시 비밀번호를 재설정해 주시기 바랍니다.\n" +
                "감사합니다.\n\n" +
                "-덕타운 운영팀";

        mailService.sendAsyncEmail(request.getEmail(), subject, text);
    }

    private String createTemporaryPwd() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz012345678!@#$%^&*-_=+";

        StringBuilder sb = new StringBuilder();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(sr.nextInt(chars.length())));
        }

        return sb.toString();
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.deleteRefreshToken();
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        userRepository.delete(user);

        // 참여중이던 채팅방 전부 나가기
        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findAllByUserId(userId);
        for (ChatRoomUser chatRoomUser : chatRoomUsers) {
            chatRoomUser.changeChatRoomUserType(ChatRoomUserType.DELETED);

            String message;
            ChatType chatType;
            if (chatRoomUser.getChatRoom().getUser().getId().equals(userId)) {
                message = "글쓴이가 채팅방을 나갔습니다. 더 이상 채팅을 전송할 수 없습니다.";
                chatType = ChatType.WRITER_EXIT;
            } else {
                message = "(알수없음)님이 채팅방을 나갔습니다.";
                chatType = ChatType.EXIT;
            }

            Chat chat = Chat.builder()
                    .chatRoom(chatRoomUser.getChatRoom())
                    .content(message)
                    .chatType(chatType)
                    .build();

            chatRepository.save(chat);

            ChatDto.MessageResponse messageResponse = ChatDto.MessageResponse.from(chat, null);
            simpMessagingTemplate.convertAndSend("/sub/chatRoom/" + chatRoomUser.getChatRoom().getId(), messageResponse);
        }

        // TODO: 추가 연관관계 해결 - 현재 게시글, 댓글, 배달팟, 채팅방 완료
    }

    private void emailDuplicateCheck(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new CustomException(EMAIL_ALREADY_EXIST);
                });
    }

    private void idDuplicateCheck(String loginId) {
        userRepository.findByLoginId(loginId)
                .ifPresent(user -> {
                    throw new CustomException(LOGIN_ID_ALREADY_EXIST);
                });
    }
}
