package vsctemplatemc.citizens;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * In-memory registry for lightweight NPC definitions. The registry intentionally avoids
 * binding to a specific NPC implementation so it can back Paper natives, Citizens, or
 * a custom entity manager.
 */
public class CitizenRegistry {

    private final Map<UUID, NpcProfile> citizens = new LinkedHashMap<>();
    private final Map<UUID, UUID> entityToCitizen = new LinkedHashMap<>();

    public NpcProfile createInnkeeper(String name, String persona) {
        return registerNpc(name, persona, Role.INNKEEPER);
    }

    public NpcProfile createMerchant(String name, String persona) {
        return registerNpc(name, persona, Role.MERCHANT);
    }

    public NpcProfile registerNpc(String name, String persona, Role role) {
        NpcProfile profile = new NpcProfile(UUID.randomUUID(), name, role, persona, Instant.now());
        citizens.put(profile.id(), profile);
        return profile;
    }

    public NpcProfile registerAndBind(String name, String persona, Role role, UUID entityId) {
        Optional<NpcProfile> existing = findByEntity(entityId);
        if (existing.isPresent()) {
            return existing.get();
        }
        NpcProfile profile = registerNpc(name, persona, role);
        entityToCitizen.put(entityId, profile.id());
        return profile;
    }

    public List<NpcProfile> findByRole(Role role) {
        return citizens.values().stream()
                .filter(profile -> profile.role() == role)
                .toList();
    }

    public List<NpcProfile> listAll() {
        return Collections.unmodifiableList(new ArrayList<>(citizens.values()));
    }

    public Collection<String> listNames() {
        return citizens.values().stream().map(NpcProfile::name).collect(Collectors.toList());
    }

    public boolean remove(UUID id) {
        return citizens.remove(id) != null;
    }

    public Optional<NpcProfile> findByEntity(UUID entityId) {
        UUID citizenId = entityToCitizen.get(entityId);
        return citizenId == null ? Optional.empty() : Optional.ofNullable(citizens.get(citizenId));
    }
}
