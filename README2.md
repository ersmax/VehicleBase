# Project01_Vehicles

Reads text commands, validates them, and stores vehicles (cars, motorbikes,
bicycles) in a single in-memory list.

## Input format

    add,<type>,key=value,key=value,...
    list

Example:

    add,macchina,ruote=4,alim=benzina,cat=strada,colore=bianco,marca=fiat,anno=2025,modello=500,porte=4,targa=el234gx,cc=1200

Vehicle types: `macchina`, `moto`, `bici`.

## Packages

| Package     | Contents                                    | Role |
|-------------|---------------------------------------------|------|
| `vehicles`  | `MainVeicoli`                               | Entry point: builds the input rows and starts the process |
| `process`   | `StartVehicle`, `StringParsing`             | Loops over the rows, reads the operation, parses strings into a key/value Map, catches errors |
| `services`  | `ListImplementation`                        | Dispatches ADD to the right vehicle type; prints the list for LIST |
|             | `VehicleAbstract`                           | Shared logic: fills the common fields of every vehicle |
|             | `Car/Bike/BicycleImplementation`            | Type-specific fields and rules, then saves to the registry |
| `singleton` | `VehicleRegistry`                           | The only data store: vehicle list, progressive id, unique plates |
| `models`    | `Vehicles`, `Car`, `Bike`, `Bicycle`        | Data classes (Car/Bike/Bicycle extend Vehicles) |
| `utils`     | `Validation`                                | Checks: required values, numbers, ranges, allowed values, booleans, plate format, year |
| `enums`     | `Operation`                                 | Allowed operations: ADD, LIST |
| `exception` | `CustomException`                           | Thrown for every invalid input |

## Flow of one row

    MainVeicoli
      └─ StartVehicle            split row, read operation, catch CustomException
           └─ ListImplementation which vehicle type? / print list
                └─ CarImplementation (or Bike / Bicycle)
                     ├─ VehicleAbstract.fillProperties   common fields + validation
                     ├─ type-specific fields + rules     (wheels, fuel, plate...)
                     └─ VehicleRegistry.add              unique plate check, assign id, store

## Validation

- **Numbers only:** `ruote`, `anno`, `cc`, `porte`, `marce`
- **Allowed values:** `alim` (fuel), `cat` (category), `colore`, `sospensione`
- **Ranges:** year between 1886 and the current year; doors, cc and gears within limits
- **Plates:** Italian format (car `AA123AA`, motorbike `AA12345`), unique, case-insensitive
- **Per type:** car ≥ 3 wheels and not `manuale`; motorbike 2 wheels; bicycle 2 wheels, `manuale` or `elettrica`
- **Booleans:** `pieghevole` accepts `si`/`no`

## Error handling

Every invalid row throws a `CustomException`. `StartVehicle` catches it, prints
`Row N skipped: <reason>`, and continues with the next row. Valid rows are stored,
and LIST prints them in insertion order with their ids.

## Design notes

- **Singleton (`VehicleRegistry`):** guarantees one shared list and one id counter,
  so plate uniqueness and progressive ids hold across all services.
- **Abstract class (`VehicleAbstract`):** common fields are written once and
  inherited by each implementation.
- **Separation of roles:** `process` parses text, `services` apply the rules,
  `singleton` stores the data.