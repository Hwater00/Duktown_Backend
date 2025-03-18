package com.duktown.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum CustomErrorType {

    // TODO ErrorCode를 추가해주세요

    // General (1xxxx)
    INVALID_HTTP_METHOD(METHOD_NOT_ALLOWED, 10001, "잘못된 Http Method 요청입니다."),
    INVALID_VALUE(BAD_REQUEST, 10002, "잘못된 입력값입니다."),
    SERVER_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, 10003, "서버 내부에 오류가 발생했습니다."),

    // Token & Authentication & Authorization (2xxxx)
    ACCESS_DENIED(FORBIDDEN, 20001, "접근 권한이 없습니다."),
    AUTHENTICATION_REQUIRED(UNAUTHORIZED, 20002, "인증이 필요한 요청입니다."),
    TOKEN_NOT_EXIST(BAD_REQUEST, 20003, "JWT Token이 존재하지 않습니다."),
    INVALID_TOKEN(BAD_REQUEST, 20004, "유효하지 않은 JWT Token입니다."),
    ACCESS_TOKEN_EXPIRED(BAD_REQUEST, 20005, "만료된 Access Token입니다."),
    UNHANDLED_TOKEN_ERROR(UNAUTHORIZED, 20006, "알 수 없는 Token 에러가 발생했습니다."),  // TODO: 추후 자연스럽게 수정

    // User (3xxxx)
    LOGIN_FAILED(UNAUTHORIZED, 30001, "아이디 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(NOT_FOUND, 30002, "사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXIST(CONFLICT, 30003, "이미 존재하는 이메일입니다."),
    LOGIN_ID_ALREADY_EXIST(CONFLICT, 30004, "이미 존재하는 아이디입니다."),
    HAVE_NO_PERMISSION(FORBIDDEN, 30005, "수정 및 삭제에 대한 권한이 없습니다."),
    LOGIN_ID_NOT_EXISTS(NOT_FOUND, 30006, "입력하신 아이디를 찾을 수 없습니다."),

    // Delivery (4xxxx)
    DELIVERY_NOT_FOUND(NOT_FOUND, 40001, "존재하지 않는 배달팟입니다."),
    INVALID_DELIVERY_SORTBY_VALUE(BAD_REQUEST, 40002, "잘못된 정렬조건입니다."),
    DELIVERY_ALREADY_CLOSED(BAD_REQUEST, 40003, "모집 마감된 배달팟입니다."),
    DELIVERY_TITLE_TOO_LONG(BAD_REQUEST, 40004, "제목 길이가 너무 깁니다. 제목은 20자까지만 작성할 수 있습니다."),

    // Post (5xxxx)
    POST_NOT_FOUND(NOT_FOUND, 50001, "존재하지 않는 게시글입니다."),
    INVALID_POST_CATEGORY_VALUE(BAD_REQUEST,50002, "잘못된 카테고리입니다."),
    POST_TITLE_TOO_LONG(BAD_REQUEST, 50003, "제목 길이가 너무 깁니다. 제목은 20자까지만 작성할 수 있습니다."),

    // Cert (6xxxx) 인증 오류
    UNABLE_TO_SEND_EMAIL(INTERNAL_SERVER_ERROR, 60001, "이메일을 전송할 수 없습니다."),
    EMAIL_CERT_NOT_FOUND(NOT_FOUND, 60002, "이메일 인증 내역이 존재하지 않습니다."),
    EMAIL_CERT_FAILED(UNAUTHORIZED, 60003, "인증번호가 일치하지 않습니다."),

    // Comment(7xxxx)
    COMMENT_NOT_FOUND(NOT_FOUND, 70001, "존재하지 않는 댓글입니다."),
    PARENT_COMMENT_NOT_FOUND(NOT_FOUND, 70002, "존재하지 않는 상위 댓글입니다."),
    DELIVERY_COMMENT_NOT_FOUND(NOT_FOUND, 70003, "배달팟에 댓글 단 내역이 존재하지 않습니다."),
    COMMENT_TARGET_NOT_SELECTED(BAD_REQUEST, 70004, "댓글을 생성하거나 조회할 대상이 선택되지 않았습니다."),
    COMMENT_DEPTH_ERROR(BAD_REQUEST, 70005, "대댓글에 대댓글을 달 수 없습니다."),
    COMMENT_TARGET_ERROR(BAD_REQUEST, 70006, "댓글을 생성하거나 조회할 대상은 하나만 선택할 수 있습니다."),
    COMMENT_TOO_LONG(BAD_REQUEST, 70007, "댓글 길이가 너무 깁니다. 255자까지만 작성할 수 있습니다."),

    // Like(8xxxx)
    LIKE_TARGET_NOT_SELECTED(BAD_REQUEST, 80001, "좋아요할 대상이 선택되지 않았습니다."),
    LIKE_TARGET_ERROR(BAD_REQUEST, 80002, "좋아요할 대상은 하나만 선택할 수 있습니다."),

    // SleepoverApply(9XXXX)
    SLEEP_OVER_APPLY_NOT_FOUND(NOT_FOUND,90001,"존재하지 않는 외박신청입니다."),
    SLEEP_OVER_APPLY_INVALID_REQUEST_TIME(BAD_REQUEST,90002,"22시 이후에는 외박 신청이 불가능합니다."),
    SLEEP_OVER_APPLY_TARGET_ERROR(BAD_REQUEST,90003,"승인 이후에는 수정 불가능합니다."),
    SLEEP_OVER_APPLY_TOTAL_ERROR(BAD_REQUEST,90004,"외박 가능 횟수를 초과했습니다"),
    SLEEP_OVER_APPLY_OVERLAPPING_DATE_ERROR(BAD_REQUEST,90005,"외박 신청 기간이 이전 내역과 중복되어 신청이 불가능합니다."),

    // Chat(10xxxx)
    CHAT_ROOM_NOT_FOUND(NOT_FOUND, 100001, "존재하지 않는 채팅방입니다."),
    NO_PERMISSION_TO_INVITE_CHAT_ROOM(FORBIDDEN, 100002, "채팅방에 초대할 수 있는 권한이 없습니다."),
    CANNOT_INVITE_SELF(BAD_REQUEST, 100003, "채팅방에 자기 자신을 초대할 수 없습니다."),
    CHAT_ROOM_USER_NOT_FOUND(NOT_FOUND, 100004, "채팅방에 존재하지 않는 사용자입니다."),
    CANNOT_SEND_WHEN_CHAT_ROOM_OWNER_EXIT(BAD_REQUEST, 100005, "채팅방 개설자가 나가 더 이상 채팅을 전송할 수 없습니다."),
    USER_ALREADY_EXISTS_IN_CHAT_ROOM(BAD_REQUEST, 100006, "이미 채팅방에 초대된 사용자입니다."),
    CANNOT_BLOCK_SELF(BAD_REQUEST, 100007, "자기 자신을 차단할 수 없습니다."),
    NO_PERMISSION_TO_BLOCK_CHAT_ROOM_USER(FORBIDDEN, 100008, "채팅방 사용자를 차단할 수 있는 권한이 없습니다."),
    BLOCKED_CHAT_ROOM_USER(BAD_REQUEST, 100009, "해당 채팅방에서 차단된 사용자입니다."),
    BLOCKED_FROM_CHAT_ROOM(BAD_REQUEST, 100010, "이용이 제한되어 더이상 참여할 수 없는 채팅방입니다."),
    DELETED_CHAT_ROOM_USER(BAD_REQUEST, 100011, "채팅방에서 나가기한 사용자입니다."),
    ALREADY_SAME_NUMBER_CHAT_ROOM_USER_EXISTS(BAD_REQUEST, 100012, "이미 같은 익명 숫자로 초대된 사용자가 존재합니다."),

    // Unit(11xxxx)
    UNIT_NOT_FOUND(NOT_FOUND, 110001, "존재하지 않는 유닛입니다."),
    UNIT_USER_NOT_FOUND(NOT_FOUND, 110002, "유닛에 존재하지 않는 사용자입니다."),

    // cleaning(12xxxx)
    CLEANING_NOT_FOUND(NOT_FOUND,120001,"존재하지 않는 청소입니다."),
    CLEANING_DATE_NOT_TODAY(BAD_REQUEST, 120002, "청소 담당일 당일에만 청소를 완료할 수 있습니다."),

    // PenaltyPoints (13xxxx)
    PENALTY_POINT_USER_NOT_SELECTED(BAD_REQUEST,130001,"적어도 한 명 이상의 사용자에게 벌점을 부여해야 합니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String errorMessage;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
