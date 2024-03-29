package com.diary.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.diary.service.CalendarService;
import com.diary.service.ScheduleService;
import com.diary.vo.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor // 클래스에 선언된 final 필드들을 매개변수로 하는 생성자를 자동으로 생성
@Controller
public class HomeController {

	private final CalendarService calendarService;
	private final ScheduleService scheduleService;

	@GetMapping("/home")
	public String home(HttpSession session, Model model,
						Integer targetYear, Integer targetMonth) {
		// 로그인 후에만 접근 가능
		if(session.getAttribute("loginMember") == null) {
			return "redirect:/login";
		}
		Member loginMember = (Member) session.getAttribute("loginMember");

		Map<String, Object> calendarMap = calendarService.getCalendar(targetYear, targetMonth);
		model.addAttribute("calendarMap", calendarMap);

		log.debug("달력 레이아웃 parameter : " + calendarMap);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("memberId", loginMember.getMemberId());
		paramMap.put("year", calendarMap.get("targetYear"));
		paramMap.put("month", (Integer) calendarMap.get("targetMonth")+1);

		List<Map<String, Object>> list = scheduleService.getScheduleListByMonth(paramMap);
		model.addAttribute("list", list);

		log.debug("달력 데이터 목록 : " + calendarMap);

		return "home";
	}

	@GetMapping("/changeCalendar")
	public String changeCalendar(HttpSession session, Model model,
								Integer targetYear, Integer targetMonth) {

		Member loginMember = (Member) session.getAttribute("loginMember");

		Map<String, Object> calendarMap = calendarService.getCalendar(targetYear, targetMonth);
		model.addAttribute("calendarMap", calendarMap);

		log.debug("달력 레이아웃 parameter : " + calendarMap);

		Map<String, Object> paramMap = new HashMap<>();

		try {
			paramMap.put("memberId", loginMember.getMemberId());
		} catch(NullPointerException e) {
			log.error("로그아웃하고 접근시 NullPointerException 발생 -> null을 반환하면 ajax error 코드 실행");
			return null;
		}
		paramMap.put("year", calendarMap.get("targetYear"));
		paramMap.put("month", (Integer) calendarMap.get("targetMonth")+1);

		List<Map<String, Object>> list = scheduleService.getScheduleListByMonth(paramMap);
		model.addAttribute("list", list);

		log.debug("달력 데이터 목록 : " + calendarMap);

		return "calendar/calendarFragment";

	}
}
