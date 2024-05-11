import 'dart:convert';
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: HashCrackScreen(),
    );
  }
}

class HashCrackScreen extends StatefulWidget {
  @override
  _HashCrackScreenState createState() => _HashCrackScreenState();
}

class _HashCrackScreenState extends State<HashCrackScreen> {
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  final TextEditingController _hashController = TextEditingController();
  final TextEditingController _maxLengthController = TextEditingController();
  String _requestId = '';
  Map<String, dynamic>? _result;
  int _progress = 0;

  late Timer _timer;

  @override
  void initState() {
    super.initState();
    _startTimer();
  }

  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }

  void _startTimer() {
    _timer = Timer.periodic(Duration(seconds: 1), (timer) {
      _fetchProgress();
    });
  }

  Future<void> _sendHashRequest() async {
    final String hash = _hashController.text;
    final String maxLength = _maxLengthController.text;

    final url = Uri.parse('http://localhost:8080/api/hash/crack');
    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'hash': hash, 'maxLength': maxLength}),
    );

    if (response.statusCode == 200) {
      final jsonResponse = jsonDecode(response.body);
      setState(() {
        _requestId = jsonResponse['requestId'];
        _result = null; // Reset result when sending a new request
      });
    } else {
      throw Exception('Failed to send hash request');
    }
  }

  Future<void> _fetchResult() async {
    final url = Uri.parse('http://localhost:8080/api/hash/status?requestId=$_requestId');
    final response = await http.get(url);

    if (response.statusCode == 200) {
      setState(() {
        _result = jsonDecode(response.body);
      });
    } else {
      throw Exception('Failed to fetch result');
    }
  }

  Future<void> _fetchProgress() async {
  final url = Uri.parse('http://localhost:8080/api/hash/percent');
  final response = await http.get(url);

  if (response.statusCode == 200) {
    final jsonResponse = jsonDecode(response.body);
    if (jsonResponse is int) {
      setState(() {
        _progress = jsonResponse;
      });
    } else {
      print('Unexpected response format: $jsonResponse');
    }
  } else {
    print('Failed to fetch progress: ${response.statusCode}');
    throw Exception('Failed to fetch progress');
  }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _scaffoldKey,
      appBar: AppBar(title: Text('MD5 Hash Crack')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextField(
              controller: _hashController,
              decoration: InputDecoration(labelText: 'Enter MD5 Hash'),
            ),
            TextField(
              controller: _maxLengthController,
              decoration: InputDecoration(labelText: 'Enter Max Length'),
            ),
            SizedBox(height: 16),
            ElevatedButton(
              onPressed: () async {
                await _sendHashRequest();
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Hash request sent')),
                );
              },
              child: Text('Send Hash Request'),
            ),
            SizedBox(height: 16),
            ElevatedButton(
              onPressed: () async {
                await _fetchResult();
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Result fetched')),
                );
              },
              child: Text('Get Result'),
            ),
            SizedBox(height: 16),
            LinearProgressIndicator(
              value: _progress / 100,
              minHeight: 10,
              backgroundColor: Colors.grey,
              valueColor: AlwaysStoppedAnimation<Color>(Colors.blue),
            ),
            SizedBox(height: 16),
            if (_result != null)
              Expanded(
                child: SingleChildScrollView(
                  child: Text(
                    'Result: ${jsonEncode(_result)}',
                    style: TextStyle(fontSize: 16),
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }
}