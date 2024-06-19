// Code installed onto the NodeMCU ESP8266 module that publishes a random into to an MQTT broker once per second via WIFI
#include <ESP8266WiFi.h>
#include <PubSubClient.h>

//WIFI data for wireless connection to NodeMCU ESP8266
const char* ssid = "MyWIFI";
const char* password = "password";
//using an open source MQTT broker offered my mosquitto.org
const char* mqtt_server = "test.mosquitto.org";

WiFiClient espClient;
PubSubClient client(espClient);
unsigned long lastMsg = 0;
#define MSG_BUFFER_SIZE (50)
char msg[MSG_BUFFER_SIZE];
int value = 0;

void setup_wifi() {
  delay(10);
  // connect to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
      // Once connected, resubscribe
      client.subscribe("cirrus/link/project");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup() {
//baud rate
  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

// every one second the ESP8266 sends a random value between 1 and 1000 to the MQTT broker
  unsigned long now = millis();
  if (now - lastMsg > 1000) {
    lastMsg = now;
    value = random(1, 1001);
    snprintf(msg, MSG_BUFFER_SIZE, "%ld", value);
    Serial.print("Publish message: ");
    Serial.println(msg);
    client.publish("cirrus/link/projectsub", msg);
  }
}
