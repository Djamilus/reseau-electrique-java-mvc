
# reseau-electrique-java-mvc
Java MVC project (Maven) to model and optimize an electrical network
=======
# Electric Network Optimization – Java (MVC)

University project developed at Université Paris Cité  
L3 Computer Science – 2025/2026

This project models, validates, and optimizes an electrical network composed of generators, houses, and connections.  
It follows a clean MVC (Model–View–Controller) architecture and includes both a console interface and a JavaFX graphical interface.

---

## Objectives

- Model an electrical network with strict structural constraints
- Guarantee network validity at all times
- Compute a cost function based on load dispersion and generator overload
- Implement an optimization algorithm inspired by Simulated Annealing
- Provide both console and graphical interfaces
- Ensure robustness through unit testing

---

## Features

- Manual network construction
- Constraint validation
- Cost computation:

  Cost = Dispersion + λ × Overload

- Simulated annealing-based optimization
- Import / Export via structured text files
- JavaFX graphical interface
- JUnit unit tests

---

## Architecture (MVC)

The project is structured following the Model–View–Controller pattern:

### Model
Contains the core business logic:
- Generators
- Houses
- Connections
- Cost computation
- Optimization algorithm

All structural constraints are enforced at this level.

### Controller
Handles user input validation and coordinates interactions between the view and the model.

### View
Two interfaces are available:
- Console interface (cross-platform)
- JavaFX graphical interface

---

## Optimization Algorithm

The optimization process is inspired by **Simulated Annealing**.

### Principle

1. Start from a valid network configuration
2. Randomly select a house
3. Temporarily modify its generator connection
4. Compute the new network cost
5. Accept or reject the modification based on:

    - Cost improvement (always accepted)
    - Probabilistic acceptance rule:

      P = exp(-Δ / T)

Where:
- Δ = cost variation
- T = current temperature

The temperature progressively decreases (cooling schedule), reducing the probability of accepting worse solutions over time.

### Guarantees

- The network always remains valid
- Each house remains connected to exactly one generator
- No entity is removed during optimization
- The best solution found is restored at the end

---

## Cost Function

The total cost is defined as:

Cost = Dispersion + λ × Surcharge

Where:

- **Dispersion** measures imbalance between generator utilization rates
- **Surcharge** penalizes generators exceeding their capacity
- **λ (lambda)** controls the weight of overload penalties

---

## House Types

| Type   | Consumption |
|--------|------------|
| BASSE  | 10 kW      |
| NORMAL | 20 kW      |
| FORTE  | 40 kW      |

---

## Testing

The project includes JUnit unit tests covering:

- Model validation
- Controller logic
- File parsing
- Optimization behavior

Run all tests with:

```
mvn test
```

## Build the project 
```
mvn clean package
```
## Run the application :
```
java -jar target/reseau-electrique-1.0-SNAPSHOT.jar instance1.txt 30
```



