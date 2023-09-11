package org.oddjob.web;


import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.input.InputRequest;
import org.oddjob.input.requests.*;
import org.oddjob.jmx.general.LocalRemoteConnection;
import org.oddjob.jmx.handlers.TaskExecutorHandlerFactory;
import org.oddjob.jmx.server.ServerInterfaceHandlerFactory;
import org.oddjob.jmx.server.ServerLoopBackException;
import org.oddjob.jmx.server.ServerSideToolkit;
import org.oddjob.jobs.tasks.*;
import org.oddjob.remote.OperationType;
import org.oddjob.remote.RemoteConnection;
import org.oddjob.remote.RemoteException;
import org.oddjob.remote.util.NotificationControl;
import org.oddjob.web.gson.GsonRemoteConnection;
import org.oddjob.web.gson.GsonUtil;
import org.oddjob.web.gson.RemoteConnectionGson;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OddjobRemotesTest {

    @Test
    void executionService() throws RemoteException, ServerLoopBackException, TaskException {

        InputMessage input0 = new InputMessage();
        input0.setMessage("Enter Some Stuff");

        InputText input1 = new InputText();
        input1.setPrompt("Name");
        input1.setProperty("your.name");

        InputPassword input2 = new InputPassword();
        input2.setPrompt("Password");
        input2.setProperty("your.password");

        InputFile input3 = new InputFile();
        // Not supported for remote.
        //        input3.setDefault("Foo.txt");
        input3.setPrompt("Your File");
        input3.setProperty("your.file");

        InputConfirm input4 = new InputConfirm();
        input4.setDefault(false);
        input4.setPrompt("I Agree");
        input4.setProperty("your.confirmation");

        InputRequest[] inputRequests = new InputRequest[]
                { input0, input1, input2, input3, input4 };

        TaskView taskView = mock(TaskView.class);

        TaskExecutor taskExecutor = mock(TaskExecutor.class);
        when(taskExecutor.getParameterInfo()).thenReturn(inputRequests);
        when(taskExecutor.execute(any(Task.class)))
                .thenReturn(taskView);

        ServerSideToolkit toolkit = mock(ServerSideToolkit.class);

        ServerInterfaceHandlerFactory<TaskExecutor, TaskExecutor> handlerFactory = new TaskExecutorHandlerFactory();

        try (RemoteConnection remoteConnection = remoteConnection(taskExecutor, handlerFactory)) {

            OperationType<InputRequest[]> operationType = OperationType.ofName("Tasks.getParameterInfo")
                    .returning(InputRequest[].class);

            InputRequest[] results = remoteConnection.invoke(1L, operationType);

            assertThat(results, is(inputRequests));

            Properties properties = new Properties();
            Task task = new BasicTask(properties);

            TaskExecutorHandlerFactory.TaskViewData taskViewLocal =
                    remoteConnection.invoke(1L, TaskExecutorHandlerFactory.EXECUTE.getOperationType(),
                            task);
        }
    }

    RemoteConnection remoteConnection(Object target, ServerInterfaceHandlerFactory<?, ?>... handlerFactories ) throws ServerLoopBackException, RemoteException {

        NotificationControl notificationControl = mock(NotificationControl.class);

        ArooaSession arooaSession = new StandardArooaSession();

        RemoteConnection local = LocalRemoteConnection.with(arooaSession, notificationControl)
                .setServerId("TEST-SERVER")
                .setExecutor(Runnable::run)
                .addHandlerFactories(handlerFactories)
                .remoteForRoot(target);

        Gson gson = GsonUtil.createGson(arooaSession);

        return RemoteConnectionGson.to(GsonRemoteConnection.to(local, gson), gson);
    }

}
