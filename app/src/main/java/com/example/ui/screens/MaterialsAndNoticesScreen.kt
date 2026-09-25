package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.ClassEntity
import com.example.data.models.NoticeEntity
import com.example.data.models.StudyMaterialEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.TuitionViewModel

@Composable
fun MaterialsAndNoticesScreen(
    viewModel: TuitionViewModel,
    initialTab: Int = 0
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(initialTab) }
    val tabs = listOf("Study Materials", "Notices & Broadcasts")

    val materials by viewModel.materials.collectAsStateWithLifecycle()
    val notices by viewModel.notices.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()

    var showUploadMaterialDialog by remember { mutableStateOf(false) }
    var showCreateNoticeDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showUploadMaterialDialog = true else showCreateNoticeDialog = true
                },
                containerColor = RoyalBlue600,
                contentColor = Color.White,
                modifier = Modifier.testTag("materials_notices_fab")
            ) {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Default.CloudUpload else Icons.Default.Campaign,
                    contentDescription = "Add"
                )
            }
        },
        containerColor = Slate50
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> {
                    // Materials Tab
                    if (materials.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No study materials uploaded yet.", color = Slate600)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(materials, key = { it.id }) { item ->
                                val cls = classes.find { it.id == item.classId }
                                MaterialCardItem(
                                    item = item,
                                    className = cls?.name ?: "All Classes",
                                    onOpen = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.urlOrPath.ifEmpty { "https://google.com" }))
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Handle exception gracefully
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Notices Tab
                    if (notices.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No notices posted yet.", color = Slate600)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(notices, key = { it.id }) { notice ->
                                val targetClass = classes.find { it.id == notice.targetClassId }?.name ?: "All Tuition Classes"
                                NoticeCardItem(notice = notice, targetClassName = targetClass)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showUploadMaterialDialog) {
        UploadMaterialDialog(
            classes = classes,
            onDismiss = { showUploadMaterialDialog = false },
            onUpload = { clsId, title, subj, ch, top, type, sz, link ->
                viewModel.addStudyMaterial(clsId, title, subj, ch, top, type, sz, link)
                showUploadMaterialDialog = false
            }
        )
    }

    if (showCreateNoticeDialog) {
        CreateNoticeDialog(
            classes = classes,
            onDismiss = { showCreateNoticeDialog = false },
            onCreate = { title, msg, targetClsId, priority, att ->
                viewModel.addNotice(title, msg, targetClsId, priority, att)
                showCreateNoticeDialog = false
            }
        )
    }
}

@Composable
fun MaterialCardItem(
    item: StudyMaterialEntity,
    className: String,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("material_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate900
                    )
                    Text(
                        text = "$className • ${item.subject} (${item.chapter})",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoyalBlue700
                    )
                }
                StatusBadge(status = item.fileType)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Topic: ${item.topic}", style = MaterialTheme.typography.bodySmall, color = Slate700)

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Size: ${item.fileSize} • Uploaded: ${item.uploadDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600
                )
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun NoticeCardItem(
    notice: NoticeEntity,
    targetClassName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notice_card_${notice.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = if (notice.priority == "Urgent") Rose600 else Amber600,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = notice.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate900
                    )
                }
                StatusBadge(status = notice.priority)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notice.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate800
            )

            if (notice.attachmentName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Attachment, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = notice.attachmentName, style = MaterialTheme.typography.labelSmall, color = RoyalBlue600)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Target: $targetClassName", style = MaterialTheme.typography.labelSmall, color = RoyalBlue700)
                Text(text = notice.publishDate, style = MaterialTheme.typography.labelSmall, color = Slate600)
            }
        }
    }
}

@Composable
fun UploadMaterialDialog(
    classes: List<ClassEntity>,
    onDismiss: () -> Unit,
    onUpload: (Long, String, String, String, String, String, String, String) -> Unit
) {
    var selectedClassId by remember { mutableStateOf(classes.firstOrNull()?.id ?: 1L) }
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Mathematics") }
    var chapter by remember { mutableStateOf("Chapter 1") }
    var topic by remember { mutableStateOf("Full Notes") }
    var fileType by remember { mutableStateOf("PDF") }
    var fileSize by remember { mutableStateOf("2.5 MB") }
    var link by remember { mutableStateOf("https://tuitionhub.example.com/material.pdf") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(text = "Upload Study Material", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Material Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = chapter,
                        onValueChange = { chapter = it },
                        label = { Text("Chapter") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fileType,
                    onValueChange = { fileType = it },
                    label = { Text("Type (PDF, Notes, Video, Link)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (title.isNotEmpty()) {
                                onUpload(selectedClassId, title, subject, chapter, topic, fileType, fileSize, link)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.weight(1f)
                    ) { Text("Publish") }
                }
            }
        }
    }
}

@Composable
fun CreateNoticeDialog(
    classes: List<ClassEntity>,
    onDismiss: () -> Unit,
    onCreate: (String, String, Long?, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Important") }
    var attachment by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(text = "Broadcast Notice", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Announcement Message *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = priority,
                    onValueChange = { priority = it },
                    label = { Text("Priority (Normal, Important, Urgent)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (title.isNotEmpty() && message.isNotEmpty()) {
                                onCreate(title, message, null, priority, attachment)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.weight(1f)
                    ) { Text("Broadcast") }
                }
            }
        }
    }
}
