package fr.unice.polytech.steats.schedule;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;


public class ScheduleHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/schedule";
    public static final int API_PORT = 5008;

    protected ScheduleHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--demo")) ScheduleManager.getInstance().demo();
        new ScheduleHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler("schedules", API_ADDRESS, new ScheduleHttpHandler(API_ADDRESS, getLogger()));
    }
}