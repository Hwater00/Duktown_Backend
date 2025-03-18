package com.duktown.domain.myPage.controller;

import com.duktown.domain.myPage.dto.PenaltyPointsDto;
import com.duktown.domain.myPage.service.MyPageService;
import com.duktown.domain.profile.dto.ProfileDto;
import com.duktown.global.security.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyPageController {
    private final MyPageService myPageService;


    // 나의 유닛 조회
    @GetMapping("/units")
    public ResponseEntity<ProfileDto.ListResponse> getMyUnits(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
       return ResponseEntity.ok(myPageService.getMyUnits(customUserDetails.getId()));
    }




}
