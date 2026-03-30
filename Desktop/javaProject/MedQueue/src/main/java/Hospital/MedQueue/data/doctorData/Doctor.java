package Hospital.MedQueue.data.doctorData;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table

public class Doctor {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private DoctorSpecialty specialty;

    private String licenseNumber;
    private String qualification;
    private int yearsOfExperience;
    private String hospitalName;
    private String department;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;
    private String email;
    private String phoneNumber;
    private String address;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
