


# SocketIO client for KMM.


## Authors
- [@Nekbakht_Zabirov](https://twitter.com/nek_zabirov)

SocketIO client for IOS & Android via Kotlin platform.

### Get started

1. Implement dependency in commonMain

### Usage

Create delegate (Callback)

```kotlin val delegate = {    
 object : SocketDelegate { override fun onConnect() { // on socket connected }    
 override fun onDisconnect() { // client dissconnected }    
 override fun onError(error: Exception) { // on error }    
override fun onMessage(body: String) { // on received json message } }} ```  
Create socket client  
```kotlin  
val socketIO = SocketIO(delegate = delegate)  
  
val endpoint = "localhost" val port = 9001  
  
/**    
 * Connect * @param endpoint host without schemes    
 * @param port connection post. basic is 9001    
 */  
 socketIO.connect(endpoint = endpoint, port = port)  
```  

Send event

```kotlin  
socketIO.sendEvent(name = "test", "{" +  
 "'name': 0" + "}")  
```