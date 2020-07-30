package wind.yang.security.domain.entity;


import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Table(name = "ACCESS_IP")
@Entity
public class AccessIp {

    @Id
    @GeneratedValue
    @Column(name = "IP_ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "IP_ADDRESS", nullable = false)
    private String ipAddress;

    @Builder
    public AccessIp(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
