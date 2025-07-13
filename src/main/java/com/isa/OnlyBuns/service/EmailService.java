package com.isa.OnlyBuns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendActivationEmail(String toEmail, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Activate your account");
        message.setText("Please click the following link to activate your account: " + activationLink);
        javaMailSender.send(message);
    }


    public void sendWeeklyStatsEmail(String toEmail, String username, int newPostsCount, int newLikesCount, int newCommentsCount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Vaša nedeljna statistika");

        String emailText = String.format(
                "Pozdrav %s,\n\nPrimijetili smo da niste posjetili našu platformu u posljednjih 7 dana. " +
                        "Evo šta se desilo u međuvremenu:\n\n" +
                        "- %d novih objava od korisnika koje pratite.\n" +
                        "- %d novih lajkova na njihovim objavama.\n" +
                        "- %d novih komentara na njihovim objavama.\n\n" +
                        "Prijavite se sada i provjerite najnoviji sadržaj koji vas čeka!\n\n" +
                        "Srdačan pozdrav,\nOnlyBuns tim",
                username, newPostsCount, newLikesCount, newCommentsCount
        );

        message.setText(emailText);
        javaMailSender.send(message);
    }



}