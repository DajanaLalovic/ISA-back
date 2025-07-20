package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.dto.LocationMessageDTO;
import com.isa.OnlyBuns.irepository.ILocationMessageRepository;
import com.isa.OnlyBuns.iservice.ILocationMessageService;
import com.isa.OnlyBuns.model.LocationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LocationMessageService implements ILocationMessageService {

    @Autowired
    private ILocationMessageRepository locationMessageRepository;
    @Override
    public LocationMessage save(LocationMessageDTO locationMessageDTO) {
        LocationMessage locationMessage = new LocationMessage();
        locationMessage.setName(locationMessageDTO.getName());
        locationMessage.setStreet(locationMessageDTO.getStreet());
        locationMessage.setNumber(locationMessageDTO.getNumber());
        locationMessage.setCity(locationMessageDTO.getCity());
        locationMessage.setPostalCode(locationMessageDTO.getPostalCode());
        locationMessage.setCountry(locationMessageDTO.getCountry());

        String fullAddress = locationMessageDTO.getStreet() + " " +
                locationMessageDTO.getNumber() + ", " +
                locationMessageDTO.getCity() + ", " +
                locationMessageDTO.getPostalCode() + ", " +
                locationMessageDTO.getCountry();

        double[] coords = getCoordinates(fullAddress);
        locationMessage.setLatitude(coords[0]);
        locationMessage.setLongitude(coords[1]);
       // locationMessage.setLatitude(0);
      //  locationMessage.setLongitude(0);
        return locationMessageRepository.save(locationMessage);
    }
    public List<LocationMessage> findAll() throws AccessDeniedException {
        return locationMessageRepository.findAll();
    }
    private double[] getCoordinates(String address) {
        try {
            String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
            String urlStr = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // Nominatim traži User-Agent

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) sb.append(line);
                String body = sb.toString();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(body);

                if (root.isArray() && root.size() > 0) {
                    JsonNode first = root.get(0);
                    // lat/lon su stringovi → .asText() pa Double.parseDouble
                    double lat = Double.parseDouble(first.path("lat").asText());
                    double lon = Double.parseDouble(first.path("lon").asText());
                    return new double[]{lat, lon};
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // zameni logovanjem
        }

        return new double[]{0, 0}; // fallback
    }
}
