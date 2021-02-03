import 'package:app/src/ui/profileFunctions.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';


class Registration extends StatefulWidget
{
  @override
  RegistrationState createState() => RegistrationState();

  @override
  Widget build(BuildContext context) {
   MaterialApp materialApp = new MaterialApp(home: new Registration());
   return materialApp;
  }
  
}

class RegistrationState extends State<Registration>
{
  bool emailIsInvalid = false;
  bool passwordIsRequired = false;
  bool phoneIsInvalid = false;
  String email = "";
  String password = "";
  String phone = "";

  void notifyEmailIsInvalid(String emailValue,String passwordValue,String phoneValue,bool emailValid,bool passwordRequired,bool phoneValid) {
    this.emailIsInvalid = emailValid;
    this.passwordIsRequired = passwordRequired;
    this.phoneIsInvalid = phoneValid;
    this.email = emailValue;
    this.password = passwordValue;
    this.phone = phoneValue;
    setState(() {
      // This call to setState tells the Flutter framework that
      // something has changed in this State, which causes it to rerun
      // the build method below so that the display can reflect the
      // updated values. If you change _counter without calling
      // setState(), then the build method won't be called again,
      // and so nothing would appear to happen.
    });
  }

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    final cursorColor = Theme.of(context).cursorColor;
    const sizedBoxSpace = SizedBox(height: 24);
    ProfileFunctions profileFunctions = new ProfileFunctions();

    final _formKey = GlobalKey<FormState>();

    TextFormField email = TextFormField(
        controller: TextEditingController(text:this.email),
        textCapitalization: TextCapitalization.words,
        cursorColor: cursorColor,
        decoration: InputDecoration(
          filled: true,
          icon: Icon(Icons.person),
          //hintText: "Your email address",
          labelText:
          "Email",
        ),
        validator: (value) {
          if(this.emailIsInvalid)
          {
            if(value.isEmpty)
            {
                return "Email is required";
            }
            return 'Email is invalid';
          }
          return null;
        },
        autovalidateMode:AutovalidateMode.always
    );
    TextFormField password = TextFormField(
        controller: TextEditingController(text:this.password),
        obscureText: true,
        decoration: InputDecoration(
            filled: true,
            icon: Icon(Icons.visibility_off),
            //hintText: "Your email address",
            labelText:
            "Password"
        ),
        validator: (value) {
          if(this.phoneIsInvalid)
          {
            if(value.isEmpty)
            {
              return "Password is required";
            }
            return 'Password is invalid';
          }
          return null;
        }
    );
    TextFormField mobile = TextFormField(
        controller: TextEditingController(text:this.phone),
        textCapitalization: TextCapitalization.words,
        cursorColor: cursorColor,
        decoration: InputDecoration(
          filled: true,
          icon: Icon(Icons.phone),
          hintText: "Your mobile number",
          labelText:
          "Mobile",
        ),
        validator: (value) {
          if(this.phoneIsInvalid)
          {
            if(value.isEmpty)
            {
              return "Phone is required";
            }
            return 'Phone is invalid';
          }
          return null;
        }
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
                        profileFunctions.showAlertDialogRegistration(context, this, email, password,mobile,"FOOD_RUNNER");
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