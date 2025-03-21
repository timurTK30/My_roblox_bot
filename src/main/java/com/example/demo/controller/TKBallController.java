package com.example.demo.controller;

import com.example.demo.domain.TKBall;
import com.example.demo.domain.TKBallData;
import com.example.demo.service.TKBallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/TKBalls")
@CrossOrigin(origins = "*")
public class TKBallController {

    private final TKBallService tkBallService;

    @GetMapping("/{chatId}")
    public ResponseEntity<TKBallData> getTKBalls(@PathVariable Long chatId) {
        TKBall tkBall = tkBallService.getTKBallByChatId(chatId).get();
        TKBallData tkBallData = new TKBallData(tkBall.getAmountOfBalls(),
                tkBall.getUser().getChatId());
        return ResponseEntity.ok(tkBallData);
    }

    @PostMapping
    public void save(@RequestBody TKBallData  tkBallData){
        System.out.println(tkBallData);
        tkBallService.updateByChatId(tkBallData.getUserChatId(), tkBallData.getAmountOfBalls());
    }
}
