package org.oddjob.web.gson.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.oddjob.input.InputRequest;
import org.oddjob.input.requests.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class InputRequestGsonTest {

    @Test
    void AllInputRequestToJsonAndBack() {

        InputMessage input0 = new InputMessage();
        input0.setMessage("Enter Some Stuff");

        InputText input1 = new InputText();
        input1.setPrompt("Name");
        input1.setProperty("your.name");

        InputPassword input2 = new InputPassword();
        input2.setPrompt("Password");
        input2.setProperty("your.password");

        InputFile input3 = new InputFile();
        input3.setPrompt("Your File");
        input3.setProperty("your.file");

        InputConfirm input4 = new InputConfirm();
        input4.setDefault(false);
        input4.setPrompt("I Agree");
        input4.setProperty("your.confirmation");

        InputRequest[] inputRequests = new InputRequest[]
                { input0, input1, input2, input3, input4 };

        Gson gson = new InputRequestGson()
                .configure(new GsonBuilder())
                .create();

        String json = gson.toJson(inputRequests);

        System.out.println(json);

        InputRequest[] copy = gson.fromJson(json, InputRequest[].class);

        assertThat(copy, is(inputRequests));
    }
}