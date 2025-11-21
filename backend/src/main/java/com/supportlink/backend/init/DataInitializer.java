package com.supportlink.backend.init;

import com.supportlink.backend.domain.*;
import com.supportlink.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final TicketRepository ticketRepository;
    private final TicketReplyRepository ticketReplyRepository;

    private final Map<Long, User> userMap = new HashMap<>();
    private final Map<Long, Agent> agentMap = new HashMap<>();
    private final Map<Long, Ticket> ticketMap = new HashMap<>();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DATA_DIR = "../databases/";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Data already exists. Skipping initialization.");
            return;
        }

        log.info("Starting data initialization...");

        loadUsers();
        loadAgents();
        loadKnowledgeBase();
        loadTickets();
        loadTicketReplies();

        log.info("Data initialization completed.");
    }

    private void loadUsers() throws IOException {
        File file = new File(DATA_DIR + "Users.csv");
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                // user_id,email,name,password_hash,created_at
                Long csvId = Long.parseLong(data[0]);
                User user = new User();
                user.setEmail(data[1]);
                user.setName(data[2]);
                user.setPasswordHash(data[3]);
                // createdAt is handled by @CreationTimestamp but we might want to set it if
                // possible.
                // Since @CreationTimestamp overwrites, we might need to use a different
                // approach or just let it be current time.
                // However, for sample data, having correct dates is nice.
                // But User entity has @CreationTimestamp which might enforce current time on
                // insert.
                // Let's try to set it, but if it's overwritten, so be it.
                // Actually, to force it, we might need to disable the auditing or use a
                // different setter if the field allows.
                // The field is updatable=false, so it's set once.
                // We can try reflection or just accept current time for now to keep it simple,
                // OR we can modify the entity to allow manual setting if null.
                // For now, let's just ignore the date from CSV or try to set it via reflection
                // if needed,
                // but standard setter is not available for createdAt usually if it's managed.
                // Wait, User.java has @Setter, so we can set it. @CreationTimestamp usually
                // only triggers if null.
                user.setCreatedAt(LocalDateTime.parse(data[4], DATE_FORMATTER));

                User savedUser = userRepository.save(user);
                userMap.put(csvId, savedUser);
            }
        }
        log.info("Loaded {} users.", userMap.size());
    }

    private void loadAgents() throws IOException {
        File file = new File(DATA_DIR + "Agents.csv");
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                // agent_id,email,name,password_hash,role
                Long csvId = Long.parseLong(data[0]);
                Agent agent = new Agent();
                agent.setEmail(data[1]);
                agent.setName(data[2]);
                agent.setPasswordHash(data[3]);
                agent.setRole(Agent.Role.valueOf(data[4].toUpperCase()));

                Agent savedAgent = agentRepository.save(agent);
                agentMap.put(csvId, savedAgent);
            }
        }
        log.info("Loaded {} agents.", agentMap.size());
    }

    private void loadKnowledgeBase() throws IOException {
        File file = new File(DATA_DIR + "KnowledgeBase.csv");
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                // article_id,category,title,content,author_id,view_count
                // Note: content might contain commas. This simple split is risky.
                // But for the sample data I generated, I didn't put commas in content.
                // If I did, I should use a proper CSV parser.
                // The sample data: "To reset your password, go to settings..." -> has comma!
                // My simple split will fail.
                // I should use a regex or a simple CSV parser logic.
                // Or just use the sample data I created which DOES have commas.
                // "To reset your password, go to settings and click reset."

                // Let's implement a basic CSV parser that handles quoted strings if my data was
                // quoted.
                // But my data wasn't quoted in the write_to_file call.
                // Wait, I wrote: 1,General,How to reset password?,To reset your password, go to
                // settings and click reset.,1,150
                // The content has a comma.
                // So split(",") will give:
                // [0] 1
                // [1] General
                // [2] How to reset password?
                // [3] "To reset your password"
                // [4] " go to settings and click reset."
                // [5] 1
                // [6] 150

                // This is a problem.
                // I should probably re-generate the CSVs with quotes or use a delimiter that is
                // not comma.
                // OR, since I know the structure, I can join the middle parts.
                // But that's hacky.

                // Alternative: I will rewrite the CSVs to be cleaner or use a pipe | delimiter?
                // The user asked to implement the PRD which uses the CSVs.
                // The CSVs I created are already there.
                // I should probably fix the CSVs to use quotes or just handle the parsing
                // better.
                // Since I created the CSVs, I can update them to be valid CSVs (with quotes) or
                // just handle the specific case.
                // Actually, the best way is to update the CSVs to use quotes for text fields.
                // But I can also just parse it intelligently:
                // I know the first 3 fields and last 2 fields are fixed. Everything in between
                // is content.

                // Let's try to parse based on index.
                // But wait, "To reset your password, go to settings and click reset."
                // It's just one comma.

                // Let's do the "join middle" strategy for now as it's robust enough for this
                // specific sample data.

                // article_id (0), category (1), title (2), content (3...N-2), author_id (N-1),
                // view_count (N)

                int n = data.length;
                Long csvId = Long.parseLong(data[0]);
                String category = data[1];
                String title = data[2];
                Long authorId = Long.parseLong(data[n - 2]);
                int viewCount = Integer.parseInt(data[n - 1]);

                StringBuilder contentBuilder = new StringBuilder();
                for (int i = 3; i <= n - 3; i++) {
                    contentBuilder.append(data[i]);
                    if (i < n - 3)
                        contentBuilder.append(",");
                }
                String content = contentBuilder.toString();

                KnowledgeBase kb = new KnowledgeBase();
                kb.setCategory(category);
                kb.setTitle(title);
                kb.setContent(content);
                kb.setViewCount(viewCount);

                Agent author = agentMap.get(authorId);
                if (author != null) {
                    kb.setAuthor(author);
                }

                knowledgeBaseRepository.save(kb);
            }
        }
        log.info("Loaded KnowledgeBase.");
    }

    private void loadTickets() throws IOException {
        File file = new File(DATA_DIR + "Tickets.csv");
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                // ticket_id,user_id,assigned_agent_id,subject,status,priority,created_at,updated_at

                Long csvId = Long.parseLong(data[0]);
                Long userId = Long.parseLong(data[1]);
                String agentIdStr = data[2];
                String subject = data[3];
                String status = data[4];
                String priority = data[5];
                String createdAtStr = data[6];
                String updatedAtStr = data[7];

                Ticket ticket = new Ticket();
                ticket.setSubject(subject);
                ticket.setStatus(Ticket.Status.valueOf(status.toUpperCase())); // Assuming Enum
                ticket.setPriority(Ticket.Priority.valueOf(priority.toUpperCase())); // Assuming Enum
                ticket.setCreatedAt(LocalDateTime.parse(createdAtStr, DATE_FORMATTER));
                ticket.setUpdatedAt(LocalDateTime.parse(updatedAtStr, DATE_FORMATTER));

                User user = userMap.get(userId);
                if (user != null)
                    ticket.setUser(user);

                if (!"null".equals(agentIdStr)) {
                    Long agentId = Long.parseLong(agentIdStr);
                    Agent agent = agentMap.get(agentId);
                    if (agent != null)
                        ticket.setAssignedAgent(agent);
                }

                Ticket savedTicket = ticketRepository.save(ticket);
                ticketMap.put(csvId, savedTicket);
            }
        }
        log.info("Loaded {} tickets.", ticketMap.size());
    }

    private void loadTicketReplies() throws IOException {
        File file = new File(DATA_DIR + "TicketReplies.csv");
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                // reply_id,ticket_id,author_id,author_type,message,created_at
                // Message might have commas. Same strategy.

                int n = data.length;
                Long csvId = Long.parseLong(data[0]);
                Long ticketId = Long.parseLong(data[1]);
                Long authorId = Long.parseLong(data[2]);
                String authorTypeStr = data[3];
                String createdAtStr = data[n - 1];

                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 4; i <= n - 2; i++) {
                    messageBuilder.append(data[i]);
                    if (i < n - 2)
                        messageBuilder.append(",");
                }
                String message = messageBuilder.toString();

                TicketReply reply = new TicketReply();
                reply.setMessage(message);
                reply.setAuthorType(TicketReply.AuthorType.valueOf(authorTypeStr.toUpperCase()));
                reply.setCreatedAt(LocalDateTime.parse(createdAtStr, DATE_FORMATTER));

                Ticket ticket = ticketMap.get(ticketId);
                if (ticket != null) {
                    reply.setTicket(ticket);
                }

                // Map author ID
                if ("User".equalsIgnoreCase(authorTypeStr)) {
                    User user = userMap.get(authorId);
                    if (user != null)
                        reply.setAuthorId(user.getUserId());
                } else {
                    Agent agent = agentMap.get(authorId);
                    if (agent != null)
                        reply.setAuthorId(agent.getAgentId());
                }

                ticketReplyRepository.save(reply);
            }
        }
        log.info("Loaded TicketReplies.");
    }
}
