package com.workitout.controller;

import com.workitout.repository.ExerciseRepository;
import com.workitout.repository.MediaRepository;
import com.workitout.repository.RoundRepository;
import com.workitout.model.Workout;
import com.workitout.repository.WorkoutRepository;
import com.workitout.model.WorkoutSchedule;
import com.workitout.repository.WorkoutScheduleRepository;
import com.workitout.model.WorkoutToPlanBinding;
import com.workitout.repository.WorkoutToPlanBindingRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yaremchuk E.N. (aka Aintech)
 */

@RestController
@RequestMapping(value = "/back/workouts")
public class WorkoutController {
    
    @Autowired
    private WorkoutRepository repo;
    
    @Autowired
    private WorkoutScheduleRepository scheduleRepo;
    
    @Autowired
    private ExerciseRepository exerRepo;
    
    @Autowired
    private RoundRepository roundRepo;
    
    @Autowired
    private MediaRepository mediaRepo;
    
    @Autowired
    private WorkoutToPlanBindingRepository bindingRepo;
    
    @GetMapping
    public Iterable<Workout> getAll () {
        return repo.findAll();
    }
    
    @GetMapping(value = "/{id}")
    public Workout get (@PathVariable Integer id) {
        return repo.findById(id).get();
    }
    
    @PostMapping
    public Workout save (@RequestBody Workout workout) {
        repo.save(workout);
        return workout;
    }
    
    @PutMapping(value = "/{id}")
    public Workout update (@PathVariable Integer id, @RequestBody Workout workout) {
        Workout work = repo.findById(id).get();
        work.setName(workout.getName());
        repo.save(work);
        return work;
    }
    
    @DeleteMapping(value = "/{id}")
    public String delete (@PathVariable Integer id) {
        Workout workout = repo.findById(id).get();
        List<WorkoutSchedule> schedules = scheduleRepo.getByWorkoutId(id);
        scheduleRepo.deleteAll(schedules);
        Iterable<WorkoutToPlanBinding> bindings = bindingRepo.getByWorkoutId(id);
        bindingRepo.deleteAll(bindings);
        workout.getExercises().forEach(exercise -> {
            exercise.getRounds().forEach(round -> roundRepo.delete(round));
            exercise.getMedias().forEach(media -> mediaRepo.delete(media));
            exerRepo.delete(exercise);
        });
        repo.delete(workout);
        return "";
    }
}
