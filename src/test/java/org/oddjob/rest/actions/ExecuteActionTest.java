package org.oddjob.rest.actions;

import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.oddjob.input.InputRequest;
import org.oddjob.input.requests.InputPassword;
import org.oddjob.input.requests.InputText;
import org.oddjob.jobs.tasks.TaskExecutor;
import org.oddjob.rest.model.WebDialog;
import org.oddjob.rest.model.WebField;
import org.oddjob.rest.model.WebForm;

import junit.framework.TestCase;

public class ExecuteActionTest extends TestCase {

	@Test
	public void testFormFieldsArePopulatedOK() {
		
		TaskExecutor taskExecutor = mock(TaskExecutor.class);

		InputText input1 = new InputText();
		input1.setProperty("favourite.fruit");
		input1.setPrompt("Favourite Fruit");
		input1.setDefault("Apples");
		
		InputPassword input2 = new InputPassword();
		input2.setPrompt("A Secret");
		input2.setProperty("some.secret");
		
		final InputRequest[] requests = new InputRequest[] {
				input1, input2 };
		
		Mockito.when(taskExecutor.getParameterInfo()).thenReturn(
				requests);
		
		Execute test = new Execute();
		
		WebForm form = test.dialogFor(taskExecutor);
		
		assertEquals(WebDialog.Type.FORM, form.getDialogType());
		
		List<WebField> fields = form.getFields();
		
		assertEquals(2, fields.size());
		
		WebField field1 = fields.get(0);
		assertEquals(WebField.Type.TEXT, field1.getFieldType());
		assertEquals("Favourite Fruit", field1.getLabel());
		assertEquals("favourite.fruit", field1.getName());
		assertEquals("Apples", field1.getValue());
		
		WebField field2 = fields.get(1);
		assertEquals(WebField.Type.PASSWORD, field2.getFieldType());
		assertEquals("A Secret", field2.getLabel());
		assertEquals("some.secret", field2.getName());
		assertEquals(null, field2.getValue());
	}
}
