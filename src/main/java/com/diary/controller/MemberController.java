package com.diary.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.diary.service.MemberService;
import com.diary.vo.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor // 클래스에 선언된 final 필드들을 매개변수로 하는 생성자를 자동으로 생성
@Controller
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/login")
	public String login(HttpSession session) {
		// 로그인이 되어 있다면 home 화면으로 redirect
		if(session.getAttribute("loginMember") != null) {
			return "redirect:/home";
		}

		return "member/loginMember";
	}

	@ResponseBody
	@PostMapping("/login")
	public String login(HttpSession session, Member paramMember) { // 오버로딩 사용

		Member loginMember = memberService.login(paramMember);

		log.debug("로그인 성공 여부 확인(실패:null) : " + loginMember);

		// 로그인 실패
		if(loginMember == null) {
			return "fail";
		}
		// 로그인 성공
		session.setAttribute("loginMember", loginMember);
		return "success";

	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {

		session.invalidate();
		return "redirect:/login";
	}

	@GetMapping("/addMember")
	public String addMember(HttpSession session) {

		if(session.getAttribute("loginMember") != null) {
			// 로그인이 되어 있는 상태
			// 리다이렉트할 컨트롤러 url
			return "redirect:/home";
		}

		return "member/addMember";
	}

	@ResponseBody
	@GetMapping("/addMemberIdCheck")
	public int addMemberIdCheck(String memberId) {

		int result = memberService.idCheck(memberId);

		log.debug("아이디 중복 체크(중복o:1,중복x:0) : " + result);

		return result;
	}

	@PostMapping("/addMember")
	public String addMember(HttpSession session, Member member) {

		if(session.getAttribute("loginMember") != null) {
			// 로그인이 되어 있는 상태
			// 리다이렉트할 컨트롤러 url
			return "redirect:/home";
		}

		int result = memberService.addMember(member);

		log.debug("회원가입(성공:1,실패:0) : " + result);

		return "redirect:/login";
	}

	@GetMapping("/updateMemberPw")
	public String updateMemberPw(HttpSession session) {

		if(session.getAttribute("loginMember") == null) {
			// 로그인이 되어 있지 않은 상태
			// 리다이렉트할 컨트롤러 url
			return "redirect:/login";
		}

		return "member/updateMemberPw";
	}

	@ResponseBody
	@PostMapping("/updateMemberPw")
	public String updateMemberPw(HttpSession session, String oldPw, String newPw) {

		if(session.getAttribute("loginMember") == null) {
			// 로그인이 되어 있지 않은 상태
			return "notLogin";
		}
		Member loginMember = (Member) session.getAttribute("loginMember");

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("memberId", loginMember.getMemberId());
		paramMap.put("oldPw", oldPw);
		paramMap.put("newPw", newPw);

		int result = memberService.updateMemberPw(paramMap);

		log.debug("회원 비밀번호 변경(성공:1,실패:0) : " + result);

		// 비밀번호 변경 실패
		if(result != 1) {
			return "fail";
		}
		// 비밀번호 변경 성공
		session.invalidate();
		return "success";
	}

	@GetMapping("/deleteMember")
	public String deleteMember(HttpSession session) {

		if(session.getAttribute("loginMember") == null) {
			// 로그인이 되어 있지 않은 상태
			// 리다이렉트할 컨트롤러 url
			return "redirect:/login";
		}

		return "member/deleteMember";
	}

	@ResponseBody
	@PostMapping("/deleteMember")
	public String deleteMember(HttpSession session, String memberPw) {

		if(session.getAttribute("loginMember") == null) {
			// 로그인이 되어 있지 않은 상태
			return "notLogin";
		}
		Member loginMember = (Member) session.getAttribute("loginMember");

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("memberId", loginMember.getMemberId());
		paramMap.put("memberPw", memberPw);

		int result = memberService.deleteMember(paramMap);

		log.debug("회원탈퇴(성공:1,실패:0) : " + result);

		// 회원 탈퇴 실패
		if(result != 1) {
			return "fail";
		}
		// 회원 탈퇴 성공
		session.invalidate();
		return "success";

	}

}
