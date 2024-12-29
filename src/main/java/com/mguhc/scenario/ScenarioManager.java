package com.mguhc.scenario;

import java.util.ArrayList;
import java.util.List;

public class ScenarioManager {
    private List<Scenario> scenarios;
    private List<Scenario> activeScenarios;

    public ScenarioManager() {
        this.scenarios = new ArrayList<>();
        this.activeScenarios = new ArrayList<>();
        loadScenarios();
    }

    private void loadScenarios() {
        // Ajoute ici les scénarios disponibles
        scenarios.add(new HasteyBoys());
        scenarios.add(new OreInInventory());
        scenarios.add(new NoStoneVariant());
        scenarios.add(new FireLess());
        // Ajoute d'autres scénarios ici
    }

    public void activateScenario(String scenarioName) {
        for (Scenario scenario : scenarios) {
            if (scenario.getName().equalsIgnoreCase(scenarioName) && !activeScenarios.contains(scenario)) {
                activeScenarios.add(scenario);
                scenario.onActivate(); // Appelle la méthode d'activation du scénario
            }
        }
    }

    public void deactivateScenario(String scenarioName) {
        for (Scenario scenario : activeScenarios) {
            if (scenario .getName().equalsIgnoreCase(scenarioName)) {
                activeScenarios.remove(scenario);
                scenario.onDeactivate(); // Appelle la méthode de désactivation du scénario
                break;
            }
        }
    }
    
    public List<Scenario> getScenarios() {
    	return scenarios;
    }
    
    public List<Scenario> getActiveScenarios() {
        return activeScenarios;
    }

    public boolean isScenarioActive(String scenarioName) {
        for (Scenario scenario : activeScenarios) {
            if (scenario.getName().equalsIgnoreCase(scenarioName)) {
                return true;
            }
        }
        return false;
    }
}