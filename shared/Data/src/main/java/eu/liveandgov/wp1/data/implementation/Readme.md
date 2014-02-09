These instances of items are defined in the following pattern

* If a single field of type *T* represents the instance, Item<T> is derived
  * Activity has one field of type String and is thereby derived from Item<String>
  * Motion has one field of type float[] and is derived from Item<float[]>
  * Tag has one field of type String and is thereby derived from Item<String>
* If a structure represents the instance of *S*, a nested class with name SStatus is created and Item<SStatus> is derived
  * GoogleActivity has a field for the activity and a field for the confidence, therefore it defines GoogleActivityStatus
  * GPS has a field for latitude, longitude and altitude, it defines GPSStatus
* If _S_ aggregates multiple items, a nested class with SItem is created and Item<List<SItem>> is derived
  * Bluetooth aggregates visible bluetooth derives, it defines BluetoothItem
  * WiFi contains a list of all access points, it defines WifiItem
* If _S_ has a always global part and aggregates a list of items, nested classes with name SStatus and SItem are created and Item<Tuple<SStatus, List<SItem>>> is derived
  * GSM has a local phone state as well as a list of cells, it defines GSMStatus and GSMItem

Data types required by nested structures are defined as nested static types of the item-instances