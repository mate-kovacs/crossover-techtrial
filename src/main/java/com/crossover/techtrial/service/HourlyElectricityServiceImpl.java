package com.crossover.techtrial.service;

import com.crossover.techtrial.dto.DailyElectricity;
import com.crossover.techtrial.model.HourlyElectricity;
import com.crossover.techtrial.repository.HourlyElectricityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * HourlyElectricityServiceImpl will handle electricity generated by a Panel.
 *
 * @author Crossover
 *
 */

@Service
public class HourlyElectricityServiceImpl implements HourlyElectricityService {
  @Autowired
  HourlyElectricityRepository hourlyElectricityRepository;
  
  public HourlyElectricity save(HourlyElectricity hourlyElectricity) {
    return hourlyElectricityRepository.save(hourlyElectricity);
  }
  
  public Page<HourlyElectricity> getAllHourlyElectricityByPanelId(Long panelId, Pageable pageable) {
    return hourlyElectricityRepository.findAllByPanelIdOrderByReadingAtDesc(panelId, pageable);
  }

  public List<DailyElectricity> getDailyElectriciryByPanelId(Long panelId) {
    List<HourlyElectricity> hourlyElectricityList = hourlyElectricityRepository.findAllByPanelIdOrderByReadingAtDesc(panelId);
    List<DailyElectricity> dailyElectricityList = new ArrayList<>();

    LocalDate currentDate = hourlyElectricityList.get(0).getReadingAt().toLocalDate();
    List<HourlyElectricity> currentHourlyElecrticityList = new ArrayList<>();
    for (HourlyElectricity electricity : hourlyElectricityList){
      if (currentDate == LocalDate.now()){
        continue;
      }
      if (currentDate.equals( electricity.getReadingAt().toLocalDate()) ){
        currentHourlyElecrticityList.add(electricity);
      } else {
        dailyElectricityList.add(createDailyElectricity(currentHourlyElecrticityList));
        currentHourlyElecrticityList.clear();
        currentDate = electricity.getReadingAt().toLocalDate();
        currentHourlyElecrticityList.add(electricity);
      }
    }
    return dailyElectricityList;
  }

  private DailyElectricity createDailyElectricity(List<HourlyElectricity> hourlyElectricityList){
    DailyElectricity result = new DailyElectricity();
    result.setDate(hourlyElectricityList.get(0).getReadingAt().toLocalDate());
    Long min = Long.MAX_VALUE;
    Long max = Long.MIN_VALUE;
    Long sum = 0L;
    Double avg;
    for (HourlyElectricity electricity : hourlyElectricityList){
      if (max < electricity.getGeneratedElectricity()){
        max = electricity.getGeneratedElectricity();
      }
      if (min > electricity.getGeneratedElectricity()){
        min = electricity.getGeneratedElectricity();
      }
      sum += electricity.getGeneratedElectricity();
    }
    avg = (double)sum / (double)hourlyElectricityList.size();
    result.setMin(min);
    result.setMax(max);
    result.setSum(sum);
    result.setAverage(avg);
    return result;
  }
  
}