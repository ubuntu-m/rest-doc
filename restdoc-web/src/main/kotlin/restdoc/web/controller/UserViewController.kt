package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import restdoc.core.Result
import restdoc.core.ok
import restdoc.web.base.auth.HolderKit
import restdoc.web.controller.obj.CreateProjectDto
import restdoc.web.controller.obj.UpdateProjectDto
import restdoc.web.model.Project
import restdoc.web.repository.ProjectRepository
import restdoc.web.util.IDUtil
import java.util.*

@RestController
@RequestMapping("/user/view")
//@Verify
class UserViewController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var holderKit: HolderKit

    @GetMapping("")
    fun list(): Result {
        val query = Query().addCriteria(Criteria("teamId").`is`(holderKit.user.teamId))
        query.with(by(desc("createTime")))
        return ok(projectRepository.list(query))
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Result = ok(mongoTemplate.findById(id, Project::class.java))

    @PostMapping("")
    fun create(@RequestBody dto: CreateProjectDto): Result {

        val project = Project(id = IDUtil.id(),
                name = dto.name,
                createTime = Date().time,
                projectId = holderKit.user.teamId,
                desc = dto.desc)
        mongoTemplate.save(project)
        return ok()
    }

    @PatchMapping("")
    fun update(@RequestBody dto: UpdateProjectDto): Result {
        projectRepository.update(Project(
                id = dto.id,
                name = dto.name,
                createTime = null,
                projectId = null,
                desc = dto.desc))
        return ok()
    }
}