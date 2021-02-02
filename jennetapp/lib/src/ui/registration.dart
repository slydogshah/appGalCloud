import 'package:app/src/ui/profileFunctions.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';


class Registration extends StatelessWidget
{
  @override
  Widget build(BuildContext context) {
   MaterialApp materialApp = new MaterialApp(home: new RegistrationScene());
   return materialApp;
  }
  
}

class RegistrationScene extends StatelessWidget {

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    final cursorColor = Theme.of(context).cursorColor;
    const sizedBoxSpace = SizedBox(height: 24);
    ProfileFunctions profileFunctions = new ProfileFunctions();

    final _formKey = GlobalKey<FormState>();

    TextFormField email = TextFormField(
                  controller: TextEditingController(),
                  textCapitalization: TextCapitalization.words,
                  cursorColor: cursorColor,
                  decoration: InputDecoration(
                    filled: true,
                    icon: Icon(Icons.person),
                    //hintText: "Your email address",
                    labelText:
                        "Email",
                  )
                );
    TextFormField password = TextFormField(
            controller: TextEditingController(),
            obscureText: true,
            decoration: InputDecoration(
                    filled: true,
                    icon: Icon(Icons.visibility_off),
                    //hintText: "Your email address",
                    labelText:
                        "Password"
            ),
            validator: (value) {
            if (value.isEmpty) {
                return 'Please enter some text';
              }
              return null;
            }
    );
    TextFormField mobile = TextFormField(
                  controller: TextEditingController(),
                  textCapitalization: TextCapitalization.words,
                  cursorColor: cursorColor,
                  decoration: InputDecoration(
                    filled: true,
                    icon: Icon(Icons.phone),
                    hintText: "Your mobile number",
                    labelText:
                        "Mobile",
                  )
                );
    Scrollbar scrollbar = new Scrollbar(child: SingleChildScrollView(
            dragStartBehavior: DragStartBehavior.down,
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                sizedBoxSpace,
                email,
                sizedBoxSpace,
                password,
                sizedBoxSpace,
                mobile,
                sizedBoxSpace,
                Center(
                  child: RaisedButton(
                    child: Text("Register"),
                    onPressed: () 
                    {
                      if (_formKey.currentState.validate()) {
                        profileFunctions.showAlertDialogRegistration(context, email.controller.text,
                            password.controller.text,int.parse(mobile.controller.text), "FOOD_RUNNER");
                      }
                    }
                  )
                ),
              ],
            )
          )
    );

    Form form = new Form(key:_formKey,child:scrollbar);
    AppBar appBar = new AppBar(automaticallyImplyLeading: false, title: new Text("Register"),);
    Scaffold scaffold = new Scaffold(appBar: appBar, body: form,);
    return scaffold;
  }
}