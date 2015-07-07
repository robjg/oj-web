
var ojFormFactory = function(divId) {

    if (divId === undefined) {
        divId = 'ojForm';
    }

    return {

        doForm: function(form, submit) {

            var formDiv$ = $('#' + divId);

            var form$ = $('<form class="actionForm">').appendTo(formDiv$);

            for (var i = 0; i < form.fields.length; ++i) {

                var field = form.fields[i];
                var field$ = $('<p>').appendTo(form$);
                var fieldId = divId + 'Field' + i;
                if (field.label != undefined) {
                    $('<label>').appendTo(field$).attr('for', fieldId).text(field.label);
                }
                if (field.name != undefined) {
                    var input$ = $('<input>').appendTo(field$);
                    input$.attr('name', field.name);

                    if (field.fieldType === 'PASSWORD') {
                        input$.attr('type', 'password');
                    }
                    else {
                        input$.attr('type', 'text');
                    }

                    input$.attr('id', fieldId);
                }
            }

            var cancel = function() {
                formDiv$.css('display', 'none');
                formDiv$.empty();
            };

            var buttons$ = $('<p>').appendTo(form$);
            buttons$.append('<input type="submit" value="OK"/>');

            $('<input type="button" value="Cancel"/>').appendTo(buttons$).click(function() {
                cancel();
            });

            form$.submit(function(event) {
                submit(form$);
                event.preventDefault();
                cancel();
            });

            formDiv$.css('display', 'block');
       }
    };
}