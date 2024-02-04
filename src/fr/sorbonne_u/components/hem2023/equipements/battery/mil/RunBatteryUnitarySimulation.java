package fr.sorbonne_u.components.hem2023.equipements.battery.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanElectricityModel;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanUserModel;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.ExternalIlluminanceModel;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.SolarPanelElectricityModel;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.SolarPanelUnitTesterModel;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.ExternalTemperatureModel;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.WaterHeatingElectricityModel;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.WaterHeatingTemperatureModel;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.WaterHeatingUnitTestModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;

public class RunBatteryUnitarySimulation {
	public static void main(String[] args) {
		try {
			Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptor =
					new HashMap<>();
			
			//launch battery
			atomicModelDescriptor.put(
					BatteryElectricityModel.URI, 
					AtomicHIOA_Descriptor.create(
							BatteryElectricityModel.class, 
							BatteryElectricityModel.URI, 
							TimeUnit.HOURS, 
							null));
			
			//launch solar pannel
			atomicModelDescriptor.put(
					SolarPanelElectricityModel.URI, 
					AtomicHIOA_Descriptor.create(
							SolarPanelElectricityModel.class, 
							SolarPanelElectricityModel.URI, 
							TimeUnit.HOURS, 
							null));
			
			atomicModelDescriptor.put(
					SolarPanelUnitTesterModel.URI, 
					AtomicHIOA_Descriptor.create(
							SolarPanelUnitTesterModel.class, 
							SolarPanelUnitTesterModel.URI, 
							TimeUnit.HOURS, 
							null));
			
			atomicModelDescriptor.put(
					ExternalIlluminanceModel.URI, 
					AtomicHIOA_Descriptor.create(
							ExternalIlluminanceModel.class, 
							ExternalIlluminanceModel.URI, 
							TimeUnit.HOURS, 
							null));
			
			//water heater
			atomicModelDescriptor.put(
					WaterHeatingElectricityModel.URI, 
					AtomicHIOA_Descriptor.create(
							WaterHeatingElectricityModel.class, 
							WaterHeatingElectricityModel.URI, 
							TimeUnit.HOURS, 
							null));
			
			atomicModelDescriptor.put(
					WaterHeatingUnitTestModel.URI, 
					AtomicHIOA_Descriptor.create(
							WaterHeatingUnitTestModel.class, 
							WaterHeatingUnitTestModel.URI, 
							TimeUnit.HOURS, 
							null));
			
			atomicModelDescriptor.put(
					ExternalTemperatureModel.URI, 
					AtomicHIOA_Descriptor.create(
							ExternalTemperatureModel.class, 
							ExternalTemperatureModel.URI, 
							TimeUnit.HOURS, 
							null));
			
			atomicModelDescriptor.put(
					WaterHeatingTemperatureModel.URI, 
					AtomicHIOA_Descriptor.create(
							WaterHeatingTemperatureModel.class, 
							WaterHeatingTemperatureModel.URI, 
							TimeUnit.HOURS, 
							null));
			
			//electric meter
			
			//fan
			atomicModelDescriptor.put(
					FanElectricityModel.MIL_URI, 
					AtomicHIOA_Descriptor.create(
							FanElectricityModel.class, 
							FanElectricityModel.MIL_URI, 
							TimeUnit.HOURS, 
							null));
			
			atomicModelDescriptor.put(
					FanUserModel.MIL_URI, 
					AtomicHIOA_Descriptor.create(
							FanUserModel.class, 
							FanUserModel.MIL_URI, 
							TimeUnit.HOURS, 
							null));
			
			//launch battery tester
			atomicModelDescriptor.put(
					BatteryUnitTesterModel.URI, 
					AtomicHIOA_Descriptor.create(
							BatteryUnitTesterModel.class, 
							BatteryUnitTesterModel.URI, 
							TimeUnit.HOURS, 
							null));
			
			Map<String, CoupledModelDescriptor> coupledModelDescriptor = new HashMap<>();
			Set<String> submodels = new HashSet<String>();
			
			//battery
			submodels.add(BatteryElectricityModel.URI);
			submodels.add(BatteryUnitTesterModel.URI);
			
			//solar pannel
			submodels.add(SolarPanelElectricityModel.URI);
			submodels.add(SolarPanelUnitTesterModel.URI);
			submodels.add(ExternalIlluminanceModel.URI);
			
			//water heater
			submodels.add(WaterHeatingElectricityModel.URI);
			submodels.add(WaterHeatingUnitTestModel.URI);
			submodels.add(ExternalTemperatureModel.URI);
			submodels.add(WaterHeatingTemperatureModel.URI);
			
			//electric meter
			
			//fan
			submodels.add(FanElectricityModel.MIL_URI);
			submodels.add(FanUserModel.MIL_URI);
			
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
