package com.example.demo.controller;

import com.example.demo.domain.TKTicket;
import com.example.demo.domain.TKTicketData;
import com.example.demo.service.TKTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/TKBalls")
@CrossOrigin(origins = "*")
public class TKTicketController {

    private final TKTicketService tkTicketService;

    @GetMapping("/{chatId}")
    public ResponseEntity<TKTicketData> getTKTickets(@PathVariable Long chatId) {
        TKTicket tkTicket = tkTicketService.getTKTicketByChatId(chatId).get();
        TKTicketData tkTicketData = new TKTicketData(tkTicket.getAmountOfTickets(),
                tkTicket.getUser().getChatId());
        return ResponseEntity.ok(tkTicketData);
    }

    @PostMapping
    public void save(@RequestBody TKTicketData tkTicketData){
        System.out.println(tkTicketData);
        tkTicketService.updateByChatId(tkTicketData.getUserChatId(), tkTicketData.getAmountOfTicket());
    }
}
