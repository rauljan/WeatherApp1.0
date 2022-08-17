package com.bwap.weatherapp.WeatherApp.views;

import com.bwap.weatherapp.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@SpringUI(path="")
public class MainView extends UI {

    @Autowired
    private WeatherService weatherService;

    private VerticalLayout mainLyout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private  Button searchButton;
    private HorizontalLayout dashboard;
    private Label location;
    private Label currentTemp;
    private HorizontalLayout mainDescriptionLayout;
    private Label weatherDescription;
    private Label maxweather;
    private Label minweather;
    private Label humidity;
    private Label pressure;
    private Label wind;
    private Label feelslike;
    private Image iconimg;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        mainLayout();
        setHeader();
        setLogo();
        setForm();
        dashboardTitle();
        dashboardDetails();
        searchButton.addClickListener(clickEvent -> {
            if (!cityTextField.getValue().equals("")){

                try {
                    updateUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else
                Notification.show("Please, enter the city");
        });
    }

    private void mainLayout() {
        iconimg = new Image();
        mainLyout = new VerticalLayout();
        mainLyout.setWidth("100%");
        mainLyout.setSpacing(true);
        mainLyout.setMargin(true);
        mainLyout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        setContent(mainLyout);
    }

    private void setHeader(){
        HorizontalLayout header = new HorizontalLayout();
        header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label title = new Label("Weather APP by Raul");

        header.addComponent(title);
        mainLyout.addComponents(header);

    }

    private void setLogo(){
        HorizontalLayout logo = new HorizontalLayout();
        logo.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Image img = new Image(null, new ClassResource("/logo.png"));
        logo.setWidth("240px");
        logo.setHeight("240px");
        logo.addComponent(img);
        mainLyout.addComponents(logo);

    }


    private void setForm(){
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        //about selection
        unitSelect = new NativeSelect<>();
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));

        formLayout.addComponent(unitSelect);

        //about cities
        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.addComponent(cityTextField);

        //search
        searchButton = new Button();
        searchButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponent(searchButton);


        mainLyout.addComponents(formLayout);

    }

    private void dashboardTitle(){
        dashboard = new HorizontalLayout();
        dashboard.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //city location
        location = new Label("Currently in Minsk");
        location.addStyleName(ValoTheme.LABEL_H2);
        location.addStyleName(ValoTheme.LABEL_LIGHT);

        //temp
        currentTemp = new Label("25C");
        currentTemp.setStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.setStyleName(ValoTheme.LABEL_H1);

        dashboard.addComponents(location, iconimg, currentTemp);

    }

    private void dashboardDetails(){
        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        VerticalLayout descriptionlayout = new VerticalLayout();
        descriptionlayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        weatherDescription = new Label("Description: Clear Sky");
        weatherDescription.setStyleName(ValoTheme.LABEL_SUCCESS);
        descriptionlayout.addComponents(weatherDescription);

        minweather = new Label("Min: 20");
        descriptionlayout.addComponents(minweather);

        maxweather = new Label("Max: 30");
        descriptionlayout.addComponents(maxweather);

        VerticalLayout pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        pressure = new Label("Pressure: 740");
        pressureLayout.addComponents(pressure);

        humidity = new Label("Humidity: 50%");
        pressureLayout.addComponents(humidity);

        wind = new Label("Wind: 14 km/h");
        pressureLayout.addComponents(wind);

        feelslike = new Label("Feels like: 30");
        pressureLayout.addComponents(feelslike);


        mainDescriptionLayout.addComponents(descriptionlayout,pressureLayout);





    }

    private void updateUI() throws JSONException {
        String city = cityTextField.getValue();
        String defaultUnit;
        weatherService.setCityName(city);

        if (unitSelect.getValue().equals("F")){
            weatherService.setUnit("imperials");
            unitSelect.setValue("F");
            defaultUnit = "\u00b0"+"F";

        } else {
            weatherService.setUnit("metric");
            defaultUnit = "\u00b0"+"C";
            unitSelect.setValue("C");
        }

        location.setValue("Currently in "+city);
        JSONObject mainObject = weatherService.returnMain();
        int temp = mainObject.getInt("temp");
        currentTemp.setValue(temp+defaultUnit);

        String iconCode = null;
        String weatherDescriptionNew = null;
        JSONArray jsonArray = weatherService.returnWeatherArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObj = jsonArray.getJSONObject(i);
            iconCode = weatherObj.getString("icon");
            weatherDescriptionNew = weatherObj.getString("description");
        }

        iconimg.setSource(new ExternalResource("http://openweathermap.org/img/wn/"+iconCode+"@2x.png"));

        weatherDescription.setValue("Description: "+weatherDescriptionNew);
        minweather.setValue("Min Temp: "+weatherService.returnMain().getInt("temp_min")+unitSelect.getValue());
        maxweather.setValue("Max Temp: "+weatherService.returnMain().getInt("temp_max")+unitSelect.getValue());
        pressure.setValue("Pressure: "+weatherService.returnMain().getInt("pressure"));
        humidity.setValue("Humidity: "+weatherService.returnMain().getInt("humidity"));
        wind.setValue("Wind: "+weatherService.returnWind().getInt("speed"));
        feelslike.setValue("Feels like: "+weatherService.returnMain().getDouble("feels_like"));

        mainLyout.addComponents(dashboard,mainDescriptionLayout);
    }

}
