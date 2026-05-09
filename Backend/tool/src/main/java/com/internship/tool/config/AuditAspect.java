package com.internship.tool.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.AuditLog;
import com.internship.tool.entity.DfdRecord;
import com.internship.tool.repository.AuditLogRepository;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogRepository auditRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* com.internship.tool.service.DfdRecordService.create(..))")
    public void createPointcut() {}

    @Pointcut("execution(* com.internship.tool.service.DfdRecordService.update(..))")
    public void updatePointcut() {}

    @Pointcut("execution(* com.internship.tool.service.DfdRecordService.softDelete(..))")
    public void deletePointcut() {}

    @AfterReturning(pointcut = "createPointcut()", returning = "result")
    public void logCreate(JoinPoint joinPoint, Object result) {
        DfdRecord record = (DfdRecord) result;
        AuditLog log = new AuditLog();
        log.setAction("CREATE");
        log.setEntityId(record.getId());
        log.setNewValue(toJson(record));
        auditRepo.save(log);
    }

    @AfterReturning(pointcut = "updatePointcut()", returning = "result")
    public void logUpdate(JoinPoint joinPoint, Object result) {
        DfdRecord record = (DfdRecord) result;
        Object[] args = joinPoint.getArgs();
        Long id = (Long) args[0];
        // For old value, we can't easily get it without querying, so maybe skip or set to null
        // In a real app, you'd store old value before update
        AuditLog log = new AuditLog();
        log.setAction("UPDATE");
        log.setEntityId(id);
        log.setNewValue(toJson(record));
        auditRepo.save(log);
    }

    @AfterReturning(pointcut = "deletePointcut()")
    public void logDelete(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long id = (Long) args[0];
        AuditLog log = new AuditLog();
        log.setAction("DELETE");
        log.setEntityId(id);
        auditRepo.save(log);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
}