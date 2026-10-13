# Project01_Vehicles

Reads text commands from a file, validates them, and stores vehicles (cars,
motorbikes, bicycles) in a single in-memory list. Vehicles can be added,
deleted, listed with filters, exported to JSON and imported back from JSON.

## Purpose

The project is a small vehicle registry driven by a command file. Its goal is to
practise the main Java topics of the course on a single realistic case:
reading and writing sequential files, validation and custom exceptions, Stream
API and lambdas, `Predicate` filters, the Singleton pattern, inheritance and
abstract classes, Lombok and SLF4J logging, **reflection** (dynamic loading of
classes and methods, with cache) and **JSON** serialization with Jackson.

## Main features

| Feature | Command | Description |
|---------|---------|-------------|
| Add a vehicle | `add,<type>,key=value,...` | Validates every parameter (format, allowed values, ranges, unique plate) and stores the vehicle with a progressive id |
| Delete a vehicle | `delete,plate=...` / `delete,id=...` | Removes a vehicle by plate or by id, whatever its type |
| List vehicles | `list` / `list,key=value,...` | Prints all vehicles, or only those matching **all** the given filters (`Predicate`), into `writeData.txt` |
| Export | `export` | Saves the whole vehicle list into `vehicles.json` |
| Import | `import` | Reads `vehicles.json` and rebuilds the list in memory (`Car`, `Bike`, `Bicycle`) |
| Error report | — | Every invalid row is logged with its row number and reason; the program continues with the next row |

Commands are read from `data/readData.txt`, one per line; operations and vehicle
classes are resolved **dynamically by name** (reflection), so new operations or
vehicle types can be added without changing the dispatcher.

## Files

| File                  | Role |
|-----------------------|------|
| `data/readData.txt`   | Input: one command per line (`//` comments and blank lines are ignored) |
| `data/writeData.txt`  | Output of every `list` command (emptied at the start of each run) |
| `data/vehicles.json`  | Written by `export`, read by `import` |

## Input format

    add,<type>,key=value,key=value,...
    delete,plate=<plate>            |   delete,id=<id>
    list                            |   list,key=value,key=value,...
    export
    import

Vehicle types in the input (only for `add`): `car`, `bike`, `bicycle`
(the type selects the service class: `car` -> `CarImplementation`).
`delete` has no type: the vehicle is found by plate (car, bike) or by id (any vehicle).

Examples:

    add,car,wheels=4,fuel=petrol,category=road,color=white,brand=fiat,year=2025,model=500,doors=4,plate=el234gx,cc=1200
    add,bike,wheels=2,fuel=petrol,category=road,color=black,brand=Yamaha,year=2025,model=r1,plate=EL22239,cc=900
    add,bicycle,wheels=2,fuel=manual,category=road,color=black,brand=Bianchi,year=2025,model=Grizl 5,gears=10,suspension=without,foldable=no
    delete,plate=el234gx
    delete,id=4
    list,color=white,brand=fiat
    export
    import

### Keys

| Key | Field | Vehicles | Allowed values |
|-----|-------|----------|----------------|
| `wheels` | number of wheels | all | number |
| `fuel` | fuel type | all | `petrol`, `diesel`, `lpg`, `methane`, `electric`, `hybrid`, `manual` |
| `category` | category | all | `road`, `citycar`, `utility`, `sedan`, `suv`, `sports`, `offroad`, `racing` |
| `color` | color | all | `white`, `black`, `grey`, `silver`, `red`, `blue`, `green`, `yellow`, `orange`, `brown` |
| `brand`, `model` | brand, model | all | text |
| `year` | production year | all | number (last 50 years) |
| `plate` | plate (unique) | car, bike | car `AA123AA`, bike `AA12345` |
| `cc` | engine size | car, bike | number |
| `doors` | doors | car | number (2-7) |
| `gears` | gears | bicycle | number (1-100) |
| `suspension` | suspensions | bicycle | `with`, `without` |
| `foldable` | foldable | bicycle | `yes`, `no` |

Values are case-insensitive. Inside the object, `vehicleType` is stored as
`car`, `bike` or `bicycle`: the same word used in the input row, in the `type`
filter and as the JSON type name.

## Packages

| Package     | Contents                                    | Role |
|-------------|---------------------------------------------|------|
| `vehicles`  | `MainVehicles`                              | Entry point: file paths, starts `StartVehicle` **by reflection** |
| `process`   | `StartVehicle`                              | Clears the output, reads the input, dispatches each row to its operation **by reflection** (with cache) |
|             | `StringParsing`                             | Splits a row on `,` and turns `key=value` items into a Map (streams) |
| `services`  | `ListImplementation`                        | Operations called by `StartVehicle`: `addVehicle`, `deleteVehicle`, `listVehicles`, `exportVehicles`, `importVehicles` |
|             | `VehicleAbstract`                           | Common fields + validation, abstract `add` (each type implements it) |
|             | `Car/Bike/BicycleImplementation`            | Type-specific fields and rules, then save to the registry |
| `singleton` | `VehicleRegistry`                           | The only data store: vehicle list, plate index, progressive id |
| `models`    | `Vehicles`, `Car`, `Bike`, `Bicycle`        | Data classes (Lombok) with the Jackson annotations for JSON |
| `utils`     | `FileManager`                               | Read the input file (stream + filters), write the output file |
|             | `JsonTransfer`                              | Export / import of the vehicle list with Jackson `ObjectMapper` |
|             | `Validation`                                | Required values, numbers, ranges, allowed values, booleans, plate format, class name builder |
| `enums`     | `Operation`                                 | Allowed operations and the name of the method that runs each one |
| `exception` | `CustomException`                           | Thrown for every invalid input |

## Flow of one row

    MainVehicles
      └─ (reflection) StartVehicle.execute()         clear writeData.txt, read readData.txt
           └─ processRows                             for each row: parse, read operation, catch CustomException
                └─ (reflection) ListImplementation.<method of the operation>
                     ├─ addVehicle
                     │    ├─ (reflection) loadService("car")  -> CarImplementation instance (cached)
                     │    └─ add(map)          polymorphism: fillProperties + type rules -> VehicleRegistry.addVehicle
                     ├─ deleteVehicle     find by plate or id (any type) -> VehicleRegistry.removeVehicle
                     ├─ listVehicles      filters -> Predicate -> stream().filter() -> writeData.txt
                     ├─ exportVehicles    registry -> JSON -> vehicles.json
                     └─ importVehicles    vehicles.json -> JSON -> registry

---

## Reflection

Reflection lets the program find a class or a method **by its name at runtime**
(`Class.forName`, `getMethod`, `invoke`) instead of writing `new XxxClass()`
or a `switch` in the code. It is used at three levels:

| Level | Where | What is found by name | Cache |
|-------|-------|-----------------------|-------|
| 0 | `MainVehicles` | the class `StartVehicle` and its method `execute` | — (called once) |
| 1 | `StartVehicle` | the operation method in `ListImplementation` (`addVehicle`, `listVehicles`, ...) | `methodsCache` (`Map<Operation, Method>`) |
| 2 | `ListImplementation` | the vehicle service class (`CarImplementation`, ...) | `vehicleCache` (`Map<String, VehicleAbstract>`) |

### Level 0: dynamic call of main (`MainVehicles`)

    Class<?> myClass = Class.forName("process.StartVehicle");          // load the class from its name
    Object object    = myClass.getDeclaredConstructor().newInstance(); // = new StartVehicle()
    Method method    = object.getClass().getMethod("execute");         // find the method by name
    method.invoke(object);                                              // = object.execute()

`MainVehicles` has no import of `StartVehicle`: the class is only a String.

### Level 1: operations (`StartVehicle`)

Each `Operation` in the enum stores the name of the method that runs it:

    ADD("addVehicle"), DELETE("deleteVehicle"), LIST("listVehicles"),
    EXPORT("exportVehicles"), IMPORT("importVehicles")

`handleOperation` replaces the old `switch`:

    Method method = getMethod(operation);              // ListImplementation.<name>(String[])
    method.invoke(service, (Object) parametersList);   // (Object) cast: invoke takes Object... varargs

- `service` is **one** `ListImplementation` instance for all rows.
- `methodsCache` (`Map<Operation, Method>`): each method is looked up once, then reused.
- Every operation has its own method with the same name pattern (`<verb>Vehicle(s)`),
  all with the same parameter (`String[]`, the whole row), so one lookup works for all.

### Level 2: vehicle services (`ListImplementation`)

`addVehicle` parses the parameters, then:

    VehicleAbstract vehicleImplementation = loadService("car");   // CarImplementation (reflection + cache)
    vehicleImplementation.add(keysValues);                         // normal call: polymorphism

- **`loadService`**: builds the class name (`car` + `implementation` -> `CarImplementation`),
  loads it with `Class.forName("services.CarImplementation")` and creates it with
  `getDeclaredConstructor().newInstance()`. The instance is stored in `vehicleCache`
  (`Map<String, VehicleAbstract>`), so each service is created **once**
  (at most 3 instances, whatever the number of rows). An unknown type
  (e.g. `add,truck,...`) gives "Vehicle type not supported".
- **`add`** is called directly, without reflection: the variable is a `VehicleAbstract`
  (where `add` is abstract), and Java runs the `add` of the real object
  (`CarImplementation.add`, `BikeImplementation.add`, ...). Since `add` is the only
  method called on a service, looking it up by name with `getMethod` and caching the
  `Method` would add code without any benefit.
- A new vehicle type needs only a new `XxxImplementation extends VehicleAbstract`
  class (plus its model): `addVehicle` does not change.

### Delete added "in a dynamic way"

Adding `delete` required only:

1. `DELETE("deleteVehicle")` in the enum;
2. a public `deleteVehicle(String[])` method in `ListImplementation`.

No change in `StartVehicle` or in the reflection code: `handleOperation` finds
`deleteVehicle` by name, exactly like `addVehicle`, `listVehicles`, `exportVehicles`
and `importVehicles`. Delete is **independent of the vehicle type**: no type in the
row and no service is loaded; it works directly on `VehicleRegistry`.
It accepts `plate` (car, bike) or `id` (any vehicle):

    delete,plate=el234gx     -> deletes the car with that plate
    delete,id=4              -> deletes vehicle 4, whatever its type
    delete                   -> "Missing parameter: plate or id"
    delete,plate=zz999zz     -> "Vehicle not found"

The old format `delete,<type>,...` is not valid anymore (the type would be read as a
parameter without `=`).

### Reflection exceptions

| Exception | When |
|-----------|------|
| `ClassNotFoundException` | no class with that name (e.g. `add,truck,...`); in `loadService` all creation errors become "Vehicle type not supported" |
| `NoSuchMethodException` | no public method with that name and parameter types (e.g. the enum names a method that does not exist) |
| `IllegalAccessException` | the method exists but is not accessible |
| `IllegalArgumentException` | wrong arguments passed to `invoke` |
| `InvocationTargetException` | the called method threw an exception: the real one is in `getCause()` |

All of them are turned into `CustomException`, so an invalid row is logged and
the program continues.

### Why reflection

With a `switch`, the dispatcher must know every option in advance. With
reflection, a new vehicle type or a new operation is added by **adding a class or
a method**, without modifying the dispatcher (open/closed principle). The cost:
typos in names are found only at runtime, which is why every reflective call is
wrapped in error handling and the lookups are cached.

---

## Filters with Predicate (`list`)

`list` alone prints all vehicles. `list` followed by one or more `key=value`
prints only the vehicles that match **all** of them:

    list
    list,color=white
    list,color=white,brand=fiat
    list,type=bicycle,color=black

How it works (`ListImplementation.listVehicles`):

1. everything after `list` is parsed by `StringParsing.mapKeyValue` into a Map
   (empty Map = no filter -> `storeAll`);
2. `storeFiltered` builds one `Predicate<Vehicles>`:

       Predicate<Vehicles> predicate = vehicle -> true;          // start: every vehicle passes
       if (filters.get("color") != null)
           predicate = predicate.and(vehicle -> vehicle.getColor().equalsIgnoreCase(filters.get("color")));
       if (filters.get("brand") != null)
           predicate = predicate.and(vehicle -> vehicle.getBrand().equalsIgnoreCase(filters.get("brand")));
       ...

   Each filter present in the row adds one condition with `.and(...)`.
   Attributes of a subclass use pattern matching, e.g.
   `vehicle instanceof Car car && String.valueOf(car.getNumberDoors()).equals(...)`,
   so vehicles without that attribute simply do not match;
3. the predicate is applied to the vehicles of the singleton:

       VehicleRegistry.getSingleton().getVehicleRegistry().stream()
                      .filter(predicate)
                      .map(Vehicles::toString)
                      .toList();

The predicate is only a **rule** (vehicle -> true/false); the real vehicles are
passed to it one by one by `stream().filter(...)`.

Supported filters: `type`, `brand`, `model`, `color`, `fuel`, `category`, `year`,
`plate`, `cc`, `doors`, `gears`, `suspension`, `foldable`.
An unknown filter (e.g. `list,speed=100`) is rejected with an error, instead of
silently printing every vehicle.

Every `list` appends a block to `writeData.txt`:

    ### list,color=white,brand=fiat (3 found) ###
    Car(super=Vehicles(id=1, ...), plate=EL234GX, cc=1200, numberDoors=4)
    ...

---

## Export / import JSON (`JsonTransfer`)

Jackson `ObjectMapper` converts objects to JSON and back. One mapper is created
once (`static`) with `SerializationFeature.INDENT_OUTPUT` (readable, indented JSON).

### Export (`export`)

    List<Vehicles> registry = VehicleRegistry.getSingleton().getVehicleRegistry();
    String json = jacksonMapper.writerFor(new TypeReference<List<Vehicles>>() {})
                               .writeValueAsString(registry);
    FileManager.writeFile(MainVehicles.JSON_FILE, List.of(json), false);   // replace the file

- `writeValueAsString` turns the whole list into one JSON text.
- `writerFor(new TypeReference<List<Vehicles>>() {})` tells Jackson the declared
  type is "list of `Vehicles`", so the type annotations of `Vehicles` are applied
  to every element.

### Import (`import`)

    String json = Files.readString(Path.of(MainVehicles.JSON_FILE));
    List<Vehicles> imported = jacksonMapper.readValue(json, new TypeReference<List<Vehicles>>() {});
    VehicleRegistry.getSingleton().loadVehiclesFromJson(imported);

- `readValue` turns the JSON text back into objects.
- `new TypeReference<List<Vehicles>>() {}`: Java forgets generic types at runtime
  (type erasure), so `List<Vehicles>.class` does not exist. The anonymous subclass
  (`{}`) keeps the full type, so Jackson knows it must build a list of `Vehicles`.
- `loadVehiclesFromJson` replaces the registry content: it keeps the original ids,
  rebuilds the plate index (so the duplicate-plate check still works) and sets the
  next id after the highest imported one.

### The annotations on `Vehicles`

The registry holds `Car`, `Bike` and `Bicycle` objects in a `List<Vehicles>`.
JSON is only text: without help, on import Jackson would not know which subclass
to create. The annotations solve this, **with no `switch`**:

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                  include = JsonTypeInfo.As.EXISTING_PROPERTY,
                  property = "vehicleType",
                  visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Car.class,     name = "car"),
        @JsonSubTypes.Type(value = Bike.class,    name = "bike"),
        @JsonSubTypes.Type(value = Bicycle.class, name = "bicycle")
    })
    public class Vehicles { ... }

| Element | Meaning |
|---------|---------|
| `@JsonTypeInfo` | each object in the JSON carries the information of **which class** it is |
| `use = Id.NAME` | the type is a short name (`"car"`), not the full class name |
| `property = "vehicleType"` | the JSON field that holds the type name |
| `include = As.EXISTING_PROPERTY` | `vehicleType` is already a field of the class: Jackson does **not** add an extra field, it uses the value returned by `getVehicleType()` |
| `visible = true` | on import, the value is used to choose the class **and** is also set on the object (`setVehicleType`); without it, `vehicleType` would be `null` after import |
| `@JsonSubTypes` | dictionary name <-> class: export writes `"vehicleType": "car"` for a `Car`, import creates a `new Car()` when it reads `"car"` |

The names in `@JsonSubTypes` must match exactly the values stored in
`vehicleType` by the implementations (`car`, `bike`, `bicycle`).

For each element, on import Jackson:

1. reads `"vehicleType"` (e.g. `"car"`);
2. finds the class in `@JsonSubTypes` (`Car`);
3. creates it with the default constructor;
4. fills it through the setters (Lombok `@Setter`): `setId`, `setPlate`, ...

On export, the fields are read through the getters (Lombok `@Getter`).

Example of `vehicles.json`:

    [ {
      "id" : 2,
      "vehicleType" : "car",
      "numberWheels" : 4,
      "fuelType" : "petrol",
      "category" : "road",
      "color" : "white",
      "brand" : "fiat",
      "productionYear" : 2026,
      "model" : "panda",
      "plate" : "FL234GX",
      "cc" : 1300,
      "numberDoors" : 4
    }, {
      "id" : 5,
      "vehicleType" : "bicycle",
      ...
      "numberGears" : 10,
      "typeSuspensions" : "without",
      "foldable" : false
    } ]

Demo: run the full input (it ends with `export`), then run an input with only
`import` and `list` (`data/readDataJson.txt`): memory starts empty, so the listed
vehicles come from the JSON file.

---

## Validation

- **Numbers only:** `wheels`, `year`, `cc`, `doors`, `gears`, `id`
- **Allowed values:** `fuel`, `category`, `color`, `suspension` (see the keys table)
- **Ranges:** year within the last 50 years; doors, cc and gears within limits
- **Plates:** Italian format (car `AA123AA`, motorbike `AA12345`), unique, case-insensitive
- **Per type:** car >= 3 wheels and not `manual`; motorbike 2 wheels and not `manual`; bicycle 2 wheels, `manual` or `electric`
- **Booleans:** `foldable` accepts `yes`/`no` (also `true`/`false`)
- **Parsing:** every parameter must be `key=value`; duplicate keys are rejected

## Error handling

Every invalid row throws a `CustomException` (reflection errors are converted
into it). `StartVehicle` catches it, logs `Row N ... <reason>` and continues
with the next row. File errors (`IOException`, including Jackson's
`JsonProcessingException`) are logged as well.

## Stream API and lambdas

- `FileManager.readFile`: `reader.lines().filter(...).toList()` drops blank and comment lines
- `StringParsing.rowParser`: `Arrays.stream(...).map(String::trim).toArray(String[]::new)`
- `StringParsing.mapKeyValue`: `stream().map(splitKeyValue).collect(Collectors.toMap(...))`
- `addVehicle`: `Arrays.stream(...).skip(2)` to drop command and type
- `deleteVehicle`: `Arrays.stream(...).skip(1)` to drop the command
- `listVehicles`: `Predicate` + `stream().filter().map(Vehicles::toString).toList()`
- `VehicleRegistry`: `findById` with `filter().findFirst()`, next id with `mapToInt().max()`

## Lombok and logging

- Models use `@Getter`, `@Setter`, `@ToString(callSuper = true)`: no hand-written
  getters, setters or `toString`.
- Classes that log use `@Slf4j` (SLF4J + slf4j-simple). Levels: `error` for failures,
  `info` for milestones, `debug` for details (enable with
  `-Dorg.slf4j.simpleLogger.defaultLogLevel=debug`).

## Design notes

- **Singleton (`VehicleRegistry`):** one shared list, one plate index and one id counter.
- **Abstract class (`VehicleAbstract`):** common fields and validation are written once
  and inherited by each implementation; `add` is abstract.
- **Type-independent delete:** `deleteVehicle` works directly on `VehicleRegistry`,
  so it does not depend on any vehicle service.
- **Reflection with cache, only where it is needed:** the operation methods (level 1)
  and the service classes (level 2) are found by name and looked up / created only
  once. Calling `add` on the service needs no reflection: polymorphism already picks
  the right implementation.
- **Separation of roles:** `process` parses text and dispatches, `services` apply the
  rules, `singleton` stores the data, `utils` handle files and JSON.

## Libraries

`lombok`, `slf4j-api`, `slf4j-simple`, `jackson-annotations`, `jackson-core`,
`jackson-databind` (in `D:/Betacom/lib`, added to the build path).
